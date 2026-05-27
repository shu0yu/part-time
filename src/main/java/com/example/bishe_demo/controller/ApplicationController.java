package com.example.bishe_demo.controller;

import com.example.bishe_demo.common.PageResultBean;
import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.entity.Application;
import com.example.bishe_demo.entity.ApplicationWithStudent;
import com.example.bishe_demo.service.ApplicationService;
import com.example.bishe_demo.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 岗位申请表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-01-16
 */
@Tag(name = "兼职申请接口")
@RestController
@RequestMapping("/application")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    /**
     * 学生申请兼职
     * @param jobId 兼职ID
     * @param applyRemark 申请备注
     * @return Result<Application>
     */
    @PostMapping("/apply/{jobId}")
    @Operation(summary = "学生申请兼职", description = "学生申请指定的兼职")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "jobId", description = "兼职ID", required = true),
            @Parameter(name = "applyRemark", description = "申请备注", required = false)
    })
    public Result<String> apply(@PathVariable Long jobId, @RequestParam(required = false) String applyRemark) {
        if (jobId == null) {
            return Result.error("兼职ID不能为空");
        }
        // 获取当前登录学生的ID
        Long studentId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }
        boolean success = applicationService.applyForJob(jobId, studentId, applyRemark);
        if (success) {
            return Result.success("申请成功");
        }
        return Result.error("已经申请过该兼职或申请失败");
    }

    /**
     * 学生查看自己的申请记录
     * @return Result<PageResultBean<Application>>
     */
    @GetMapping("/myApplications")
    @Operation(summary = "学生查看自己的申请记录", description = "获取当前登录学生的所有申请记录")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "pageSize", description = "每页显示的条数", required = true),
            @Parameter(name = "currentPage", description = "要查询的页", required = true)
    })
    public Result<PageResultBean<Application>> myApplications(@RequestParam int pageSize, @RequestParam int currentPage) {
        // 获取当前登录学生的ID
        Long studentId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }
        var page = applicationService.getStudentApplications(studentId, pageSize, currentPage);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 企业查看收到的申请记录（包含学生信息）
     * @param jobId 兼职 ID
     * @return Result<PageResultBean<ApplicationWithStudent>>
     */
    @GetMapping("/jobApplicationsWithStudent/{jobId}")
    @Operation(summary = "企业查看收到的申请记录（含学生信息）", description = "获取指定兼职的所有申请记录，包含学生的详细信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "jobId", description = "兼职 ID", required = true),
            @Parameter(name = "pageSize", description = "每页显示的条数", required = true),
            @Parameter(name = "currentPage", description = "要查询的页", required = true)
    })
    public Result<PageResultBean<ApplicationWithStudent>> jobApplicationsWithStudent(@PathVariable Long jobId, @RequestParam int pageSize, @RequestParam int currentPage) {
        if (jobId == null) {
            return Result.error("兼职 ID 不能为空");
        }
        var page = applicationService.getJobApplicationsWithStudentInfo(jobId, pageSize, currentPage);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 学生取消申请
     * @param applicationId 申请ID
     * @return Result<String>
     */
    @DeleteMapping("/cancel/{applicationId}")
    @Operation(summary = "学生取消申请", description = "学生取消自己的兼职申请")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "applicationId", description = "申请ID", required = true)
    })
    public Result<String> cancelApplication(@PathVariable Long applicationId) {
        if (applicationId == null) {
            return Result.error("申请ID不能为空");
        }
        Long studentId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }
        boolean success = applicationService.cancelApplication(applicationId, studentId);
        if (success) {
            return Result.success("取消申请成功");
        }
        return Result.error("取消申请失败");
    }
}
