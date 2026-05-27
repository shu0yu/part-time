package com.example.bishe_demo.service;

import com.example.bishe_demo.entity.Job;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.math.BigDecimal;

/**
 * <p>
 * 兼职岗位表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface JobService extends IService<Job> {

    IPage<Job> pageList(int pageSize, int currentPage, String jobName, String workAddress);

    IPage<Job> getCompanyJobs(Long companyId, int pageSize, int currentPage);

    IPage<Job> searchJobs(int pageSize, int currentPage, String keyword, String jobType, 
                         String workAddress, BigDecimal salaryMin, BigDecimal salaryMax);

    /**
     * 管理员查看所有岗位（包括已下架的）
     * @param pageSize 每页条数
     * @param currentPage 当前页码
     * @param workAddress 地区筛选（可选）
     * @param companyId 企业ID筛选（可选）
     * @return 分页结果
     */
    IPage<Job> getAllJobsForAdmin(int pageSize, int currentPage, String workAddress, Long companyId);

    /**
     * 管理员下架违规岗位
     * @param jobId 岗位ID
     * @return 是否成功
     */
    boolean takeDownJob(Long jobId);
}
