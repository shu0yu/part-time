package com.example.bishe_demo.service;

import com.example.bishe_demo.entity.Application;
import com.example.bishe_demo.entity.ApplicationWithStudent;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * <p>
 * 岗位申请表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface ApplicationService extends IService<Application> {

    IPage<Application> getStudentApplications(Long studentId, int pageSize, int currentPage);

    IPage<Application> getJobApplications(Long jobId, int pageSize, int currentPage);

    IPage<ApplicationWithStudent> getJobApplicationsWithStudentInfo(Long jobId, int pageSize, int currentPage);

    boolean applyForJob(Long jobId, Long studentId, String applyRemark);

    boolean cancelApplication(Long applicationId, Long studentId);
}
