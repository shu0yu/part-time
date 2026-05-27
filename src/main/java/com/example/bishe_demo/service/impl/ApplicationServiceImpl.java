package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bishe_demo.entity.Application;
import com.example.bishe_demo.entity.ApplicationWithStudent;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.mapper.ApplicationMapper;
import com.example.bishe_demo.mapper.UserMapper;
import com.example.bishe_demo.service.ApplicationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 岗位申请表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<Application> getStudentApplications(Long studentId, int pageSize, int currentPage) {
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Application::getStudentId, studentId);
        IPage<Application> page = new Page<>(currentPage, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Application> getJobApplications(Long jobId, int pageSize, int currentPage) {
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Application::getJobId, jobId);
        IPage<Application> page = new Page<>(currentPage, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<ApplicationWithStudent> getJobApplicationsWithStudentInfo(Long jobId, int pageSize, int currentPage) {
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Application::getJobId, jobId);
        IPage<Application> page = new Page<>(currentPage, pageSize);
        IPage<Application> applicationPage = baseMapper.selectPage(page, queryWrapper);
        
        List<ApplicationWithStudent> result = new ArrayList<>();
        for (Application app : applicationPage.getRecords()) {
            ApplicationWithStudent dto = new ApplicationWithStudent();
            dto.setId(app.getId());
            dto.setJobId(app.getJobId());
            dto.setStudentId(app.getStudentId());
            dto.setApplyRemark(app.getApplyRemark());
            
            User student = userMapper.selectById(app.getStudentId());
            if (student != null) {
                dto.setStudentUsername(student.getUsername());
                dto.setStudentRealName(student.getRealName());
                dto.setStudentPhone(student.getPhone());
            }
            
            result.add(dto);
        }
        
        IPage<ApplicationWithStudent> resultPage = new Page<>(currentPage, pageSize);
        resultPage.setTotal(applicationPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }

    @Override
    public boolean applyForJob(Long jobId, Long studentId, String applyRemark) {
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Application::getJobId, jobId);
        queryWrapper.eq(Application::getStudentId, studentId);
        if (baseMapper.selectOne(queryWrapper) != null) {
            return false;
        }
        Application application = new Application();
        application.setJobId(jobId);
        application.setStudentId(studentId);
        application.setApplyRemark(applyRemark);
        return baseMapper.insert(application) > 0;
    }

    @Override
    public boolean cancelApplication(Long applicationId, Long studentId) {
        if (applicationId == null || studentId == null) {
            return false;
        }
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Application::getId, applicationId);
        queryWrapper.eq(Application::getStudentId, studentId);
        Application application = baseMapper.selectOne(queryWrapper);
        if (application == null) {
            return false;
        }
        return baseMapper.deleteById(applicationId) > 0;
    }
}
