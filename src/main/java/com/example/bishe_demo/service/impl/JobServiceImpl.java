package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bishe_demo.constant.SystemConstant;
import com.example.bishe_demo.entity.CompanyInfo;
import com.example.bishe_demo.entity.Job;
import com.example.bishe_demo.mapper.JobMapper;
import com.example.bishe_demo.service.CompanyInfoService;
import com.example.bishe_demo.service.JobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 兼职岗位表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final CompanyInfoService companyInfoService;

    /**
     * 构造函数注入依赖
     * @param companyInfoService 企业信息服务
     */
    public JobServiceImpl(CompanyInfoService companyInfoService) {
        this.companyInfoService = companyInfoService;
    }

    /**
     * 批量填充岗位列表的公司名称
     * @param jobs 岗位列表
     */
    private void fillCompanyNames(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return;
        }
        
        List<Long> companyIds = jobs.stream()
            .map(Job::getCompanyId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());
        
        if (companyIds.isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<CompanyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CompanyInfo::getUserId, companyIds);
        List<CompanyInfo> companyInfos = companyInfoService.list(queryWrapper);
        
        Map<Long, String> companyNameMap = companyInfos.stream()
            .filter(info -> info.getUserId() != null)
            .collect(Collectors.toMap(
                CompanyInfo::getUserId,
                CompanyInfo::getCompanyName,
                (v1, v2) -> v1
            ));
        
        for (Job job : jobs) {
            if (job.getCompanyId() != null) {
                job.setCompanyName(companyNameMap.get(job.getCompanyId()));
            }
        }
    }

    /**
     * 填充单个岗位的公司名称
     * @param job 岗位信息
     */
    private void fillCompanyName(Job job) {
        if (job == null || job.getCompanyId() == null) {
            return;
        }
        
        CompanyInfo companyInfo = companyInfoService.getByUserId(job.getCompanyId());
        if (companyInfo != null) {
            job.setCompanyName(companyInfo.getCompanyName());
        }
    }

    /**
     * 分页查询兼职岗位列表
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param jobName 岗位名称（模糊查询）
     * @param workAddress 工作地点（模糊查询）
     * @return 岗位分页结果
     */
    @Override
    public IPage<Job> pageList(int pageSize, int currentPage, String jobName, String workAddress) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getIsDelete, SystemConstant.NOT_DELETED);
        
        if (jobName != null && !jobName.trim().isEmpty()) {
            queryWrapper.like(Job::getJobName, jobName);
        }
        if (workAddress != null && !workAddress.trim().isEmpty()) {
            queryWrapper.like(Job::getWorkAddress, workAddress);
        }
        
        IPage<Job> page = new Page<>(currentPage, pageSize);
        IPage<Job> result = baseMapper.selectPage(page, queryWrapper);
        fillCompanyNames(result.getRecords());
        return result;
    }

    /**
     * 分页查询企业发布的岗位
     * @param companyId 企业用户ID
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @return 岗位分页结果
     */
    @Override
    public IPage<Job> getCompanyJobs(Long companyId, int pageSize, int currentPage) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getCompanyId, companyId);
        queryWrapper.eq(Job::getIsDelete, SystemConstant.NOT_DELETED);
        
        IPage<Job> page = new Page<>(currentPage, pageSize);
        IPage<Job> result = baseMapper.selectPage(page, queryWrapper);
        fillCompanyNames(result.getRecords());
        return result;
    }

    /**
     * 多条件搜索岗位
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param keyword 关键词（岗位名称或描述）
     * @param jobType 岗位类型
     * @param workAddress 工作地点
     * @param salaryMin 最低薪资
     * @param salaryMax 最高薪资
     * @return 岗位分页结果
     */
    @Override
    public IPage<Job> searchJobs(int pageSize, int currentPage, String keyword, String jobType, 
                                 String workAddress, BigDecimal salaryMin, BigDecimal salaryMax) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getIsDelete, SystemConstant.NOT_DELETED);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(Job::getJobName, keyword)
                       .or().like(Job::getJobDesc, keyword);
            });
        }
        
        if (jobType != null && !jobType.trim().isEmpty()) {
            queryWrapper.eq(Job::getJobType, jobType);
        }
        
        if (workAddress != null && !workAddress.trim().isEmpty()) {
            queryWrapper.like(Job::getWorkAddress, workAddress);
        }
        
        if (salaryMin != null) {
            queryWrapper.ge(Job::getSalaryMin, salaryMin);
        }
        if (salaryMax != null) {
            queryWrapper.le(Job::getSalaryMax, salaryMax);
        }
        
        IPage<Job> page = new Page<>(currentPage, pageSize);
        IPage<Job> result = baseMapper.selectPage(page, queryWrapper);
        fillCompanyNames(result.getRecords());
        return result;
    }

    /**
     * 管理员分页查询所有岗位
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param workAddress 工作地点
     * @param companyId 企业ID
     * @return 岗位分页结果
     */
    @Override
    public IPage<Job> getAllJobsForAdmin(int pageSize, int currentPage, String workAddress, Long companyId) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        
        if (workAddress != null && !workAddress.trim().isEmpty()) {
            queryWrapper.like(Job::getWorkAddress, workAddress);
        }
        
        if (companyId != null) {
            queryWrapper.eq(Job::getCompanyId, companyId);
        }
        
        IPage<Job> page = new Page<>(currentPage, pageSize);
        IPage<Job> result = baseMapper.selectPage(page, queryWrapper);
        fillCompanyNames(result.getRecords());
        return result;
    }

    /**
     * 下架岗位（软删除）
     * @param jobId 岗位ID
     * @return 是否下架成功
     */
    @Override
    public boolean takeDownJob(Long jobId) {
        Job job = new Job();
        job.setId(jobId);
        job.setIsDelete(SystemConstant.DELETED);
        return updateById(job);
    }
}
