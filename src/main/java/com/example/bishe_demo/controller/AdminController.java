package com.example.bishe_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bishe_demo.annotation.RequiredRole;
import com.example.bishe_demo.common.PageResultBean;
import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.entity.Application;
import com.example.bishe_demo.entity.ChatRecord;
import com.example.bishe_demo.entity.CompanyInfo;
import com.example.bishe_demo.entity.Job;
import com.example.bishe_demo.entity.StudentInfo;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.service.ApplicationService;
import com.example.bishe_demo.service.ChatRecordService;
import com.example.bishe_demo.service.CompanyInfoService;
import com.example.bishe_demo.service.JobService;
import com.example.bishe_demo.service.StudentInfoService;
import com.example.bishe_demo.service.UserService;
import com.example.bishe_demo.vo.AdminChatSessionVO;
import com.example.bishe_demo.vo.DisputeVO;
import com.example.bishe_demo.vo.UserWithRoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员功能控制器
 */
@Tag(name = "管理员接口")
@RestController
@RequestMapping("/admin")
@RequiredRole("ADMIN")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ChatRecordService chatRecordService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private CompanyInfoService companyInfoService;

    /**
     * 查看所有用户信息（包含角色）
     */
    @GetMapping("/users")
    @Operation(summary = "查看所有用户信息", description = "管理员查看所有用户信息，支持按角色和状态筛选")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "pageSize", description = "每页显示条数", required = true),
            @Parameter(name = "currentPage", description = "当前页码", required = true),
            @Parameter(name = "roleName", description = "角色名称筛选（ADMIN/COMPANY/STUDENT）", required = false),
            @Parameter(name = "status", description = "状态筛选（0-禁用，1-正常）", required = false)
    })
    public Result<PageResultBean<UserWithRoleVO>> getAllUsers(
            @RequestParam int pageSize,
            @RequestParam int currentPage,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Byte status) {
        IPage<UserWithRoleVO> page = userService.getAllUsersWithRole(pageSize, currentPage, roleName, status);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 禁用/启用用户账号
     */
    @PutMapping("/user/{userId}/status")
    @Operation(summary = "禁用/启用用户账号", description = "管理员修改用户账号状态")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "userId", description = "用户ID", required = true),
            @Parameter(name = "status", description = "状态（0-禁用，1-正常）", required = true)
    })
    public Result<String> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Byte status) {
        boolean success = userService.updateUserStatus(userId, status);
        if (success) {
            return Result.success(status == 0 ? "账号已禁用" : "账号已启用");
        }
        return Result.error("操作失败");
    }

    /**
     * 查看所有岗位
     */
    @GetMapping("/jobs")
    @Operation(summary = "查看所有岗位", description = "管理员查看所有岗位，支持按地区和企业筛选")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "pageSize", description = "每页显示条数", required = true),
            @Parameter(name = "currentPage", description = "当前页码", required = true),
            @Parameter(name = "workAddress", description = "地区筛选", required = false),
            @Parameter(name = "companyId", description = "企业ID筛选", required = false)
    })
    public Result<PageResultBean<Job>> getAllJobs(
            @RequestParam int pageSize,
            @RequestParam int currentPage,
            @RequestParam(required = false) String workAddress,
            @RequestParam(required = false) Long companyId) {
        IPage<Job> page = jobService.getAllJobsForAdmin(pageSize, currentPage, workAddress, companyId);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 下架违规岗位
     */
    @PutMapping("/job/{jobId}/takeDown")
    @Operation(summary = "下架违规岗位", description = "管理员下架违规岗位")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "jobId", description = "岗位ID", required = true)
    })
    public Result<String> takeDownJob(@PathVariable Long jobId) {
        boolean success = jobService.takeDownJob(jobId);
        if (success) {
            return Result.success("岗位已下架");
        }
        return Result.error("操作失败");
    }

    /**
     * 查看纠纷信息（包含聊天记录和申请记录）
     */
    @GetMapping("/dispute")
    @Operation(summary = "查看纠纷信息", description = "管理员查看学生和企业的沟通记录和申请记录")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "studentId", description = "学生用户ID", required = true),
            @Parameter(name = "companyId", description = "企业用户ID", required = true),
            @Parameter(name = "jobId", description = "岗位ID", required = true)
    })
    public Result<DisputeVO> getDisputeInfo(
            @RequestParam Long studentId,
            @RequestParam Long companyId,
            @RequestParam Long jobId) {
        DisputeVO disputeVO = new DisputeVO();
        
        Job job = jobService.getById(jobId);
        if (job != null) {
            disputeVO.setJobId(job.getId());
            disputeVO.setJobName(job.getJobName());
        }
        
        disputeVO.setStudentId(studentId);
        User studentUser = userService.getUserById(studentId);
        if (studentUser != null) {
            disputeVO.setStudentName(studentUser.getRealName());
        }
        
        disputeVO.setCompanyId(companyId);
        CompanyInfo companyInfo = companyInfoService.getByUserId(companyId);
        if (companyInfo != null) {
            disputeVO.setCompanyName(companyInfo.getCompanyName());
        }
        
        List<Application> applications = applicationService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Application>()
                .eq(Application::getJobId, jobId)
                .eq(Application::getStudentId, studentId)
        );
        if (!applications.isEmpty()) {
            disputeVO.setApplication(applications.get(0));
        }
        
        List<ChatRecord> chatRecords = chatRecordService.getChatHistory(studentId, companyId, jobId);
        disputeVO.setChatRecords(chatRecords);
        
        return Result.success(disputeVO);
    }

    /**
     * 管理员获取所有聊天会话
     */
    @GetMapping("/chat/sessions")
    @Operation(summary = "获取所有聊天会话", description = "管理员获取平台所有用户的聊天会话")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<List<AdminChatSessionVO>> getAllChatSessions() {
        List<AdminChatSessionVO> sessions = chatRecordService.getAllChatSessionsForAdmin();
        return Result.success(sessions);
    }

    /**
     * 管理员获取聊天历史（不限制当前用户）
     */
    @GetMapping("/chat/history")
    @Operation(summary = "获取聊天历史", description = "管理员获取任意两个用户之间的聊天历史")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "userId1", description = "用户1 ID", required = true),
            @Parameter(name = "userId2", description = "用户2 ID", required = true),
            @Parameter(name = "jobId", description = "关联的岗位 ID", required = false)
    })
    public Result<List<ChatRecord>> getChatHistoryForAdmin(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(required = false) Long jobId) {
        List<ChatRecord> records = chatRecordService.getChatHistory(userId1, userId2, jobId);
        return Result.success(records);
    }
}
