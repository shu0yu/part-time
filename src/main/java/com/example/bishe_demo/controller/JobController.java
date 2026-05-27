package com.example.bishe_demo.controller;

import com.example.bishe_demo.annotation.RequiredRole;
import com.example.bishe_demo.common.PageResultBean;
import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.constant.SystemConstant;
import com.example.bishe_demo.entity.Job;
import com.example.bishe_demo.service.CompanyInfoService;
import com.example.bishe_demo.service.JobService;
import com.example.bishe_demo.utils.UserIdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 兼职岗位表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-01-16
 */
@Tag(name = "兼职管理接口")
@RestController
@RequestMapping("/job")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class JobController {
    
    private final JobService jobService;
    private final CompanyInfoService companyInfoService;

    /**
     * 构造函数注入依赖
     * @param jobService 兼职服务
     * @param companyInfoService 企业信息服务
     */
    public JobController(JobService jobService, CompanyInfoService companyInfoService) {
        this.jobService = jobService;
        this.companyInfoService = companyInfoService;
    }

    /**
     * 发布兼职信息
     * @param job 兼职信息
     * @return 发布结果，成功则返回兼职信息
     */
    @PostMapping("/add")
    @RequiredRole("COMPANY")
    @Operation(summary = "发布兼职信息", description = "企业发布新的兼职信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<Job> add(@RequestBody Job job) {
        if (job == null || job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            return Result.error("兼职标题不能为空");
        }
        
        Long userId = UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户状态异常");
        }
        
        job.setCompanyId(userId);
        job.setIsDelete(SystemConstant.NOT_DELETED);
        
        boolean saved = jobService.save(job);
        if (saved) {
            return Result.success(job);
        }
        return Result.error("发布失败");
    }

    /**
     * 更新兼职信息
     * @param job 兼职信息
     * @return 更新结果，成功则返回兼职信息
     */
    @PutMapping("/update")
    @RequiredRole("COMPANY")
    @Operation(summary = "更新兼职信息", description = "修改兼职信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<Job> update(@RequestBody Job job) {
        if (job == null || job.getId() == null) {
            return Result.error("兼职ID不能为空");
        }
        
        boolean updated = jobService.updateById(job);
        if (updated) {
            return Result.success(job);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除兼职信息（软删除）
     * @param id 兼职ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole("COMPANY")
    @Operation(summary = "删除兼职信息", description = "根据ID删除兼职信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "id", description = "兼职ID", required = true)
    })
    public Result<String> delete(@PathVariable Long id) {
        Job job = new Job();
        job.setId(id);
        job.setIsDelete(SystemConstant.DELETED);
        
        boolean updated = jobService.updateById(job);
        if (updated) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 根据ID查询兼职信息
     * @param id 兼职ID
     * @return 兼职详细信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "根据ID查询兼职信息", description = "根据兼职ID获取兼职详细信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "id", description = "兼职ID", required = true)
    })
    public Result<Job> getById(@PathVariable Long id) {
        Job job = jobService.getById(id);
        if (job != null && job.getIsDelete() == SystemConstant.NOT_DELETED) {
            if (job.getCompanyId() != null) {
                var companyInfo = companyInfoService.getByUserId(job.getCompanyId());
                if (companyInfo != null) {
                    job.setCompanyName(companyInfo.getCompanyName());
                }
            }
            return Result.success(job);
        }
        return Result.error("兼职不存在");
    }

    /**
     * 查询当前企业发布的兼职信息
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @return 企业发布的兼职列表
     */
    @GetMapping("/myJobs")
    @RequiredRole("COMPANY")
    @Operation(summary = "查询当前企业发布的兼职信息", description = "获取当前登录企业发布的所有兼职信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "pageSize", description = "每页显示的条数", required = true),
            @Parameter(name = "currentPage", description = "要查询的页", required = true)
    })
    public Result<PageResultBean<Job>> myJobs(@RequestParam int pageSize, @RequestParam int currentPage) {
        Long userId = UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户状态异常");
        }
        
        var page = jobService.getCompanyJobs(userId, pageSize, currentPage);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 查询所有未删除的兼职信息（学生可查看）
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param jobName 岗位名称（模糊查询）
     * @param workAddress 工作地点（模糊查询）
     * @return 兼职列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有兼职信息", description = "获取所有未删除的兼职信息")
    @Parameters({
            @Parameter(name = "pageSize", description = "每页显示的条数", required = true),
            @Parameter(name = "currentPage", description = "要查询的页", required = true),
            @Parameter(name = "jobName", description = "岗位名称", required = false),
            @Parameter(name = "workAddress", description = "工作地点", required = false)
    })
    public Result<PageResultBean<Job>> list(@RequestParam int pageSize, @RequestParam int currentPage, 
                                           @RequestParam(required = false) String jobName, 
                                           @RequestParam(required = false) String workAddress) {
        var page = jobService.pageList(pageSize, currentPage, jobName, workAddress);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 多条件检索兼职信息
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param keyword 关键词（岗位名称或描述）
     * @param jobType 岗位类型
     * @param workAddress 工作地点
     * @param salaryMin 最低薪资
     * @param salaryMax 最高薪资
     * @return 符合条件的兼职列表
     */
    @GetMapping("/search")
    @Operation(summary = "多条件检索兼职信息", description = "根据多个条件检索兼职信息")
    @Parameters({
            @Parameter(name = "pageSize", description = "每页显示的条数", required = true),
            @Parameter(name = "currentPage", description = "要查询的页", required = true),
            @Parameter(name = "keyword", description = "关键词", required = false),
            @Parameter(name = "jobType", description = "岗位类型", required = false),
            @Parameter(name = "workAddress", description = "工作地点", required = false),
            @Parameter(name = "salaryMin", description = "最低薪资", required = false),
            @Parameter(name = "salaryMax", description = "最高薪资", required = false)
    })
    public Result<PageResultBean<Job>> search(@RequestParam int pageSize, @RequestParam int currentPage, 
                                             @RequestParam(required = false) String keyword, 
                                             @RequestParam(required = false) String jobType, 
                                             @RequestParam(required = false) String workAddress, 
                                             @RequestParam(required = false) BigDecimal salaryMin, 
                                             @RequestParam(required = false) BigDecimal salaryMax) {
        var page = jobService.searchJobs(pageSize, currentPage, keyword, jobType, workAddress, salaryMin, salaryMax);
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }
}
