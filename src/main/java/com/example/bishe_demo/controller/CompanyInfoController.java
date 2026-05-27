package com.example.bishe_demo.controller;

import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.entity.CompanyInfo;
import com.example.bishe_demo.service.CompanyInfoService;
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
 * 企业信息扩展表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Tag(name = "企业信息接口")
@RestController
@RequestMapping("/company")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class CompanyInfoController {
    @Autowired
    private CompanyInfoService companyInfoService;

    /**
     * 保存或更新企业信息
     * @param companyInfo 企业信息
     * @return Result<CompanyInfo>
     */
    @PostMapping("/save")
    @Operation(summary = "保存或更新企业信息", description = "企业保存或更新自己的信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<CompanyInfo> save(@RequestBody CompanyInfo companyInfo) {
        // 获取当前登录用户的ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        companyInfo.setUserId(userId);
        // 检查是否已存在企业信息
        CompanyInfo existingInfo = companyInfoService.getByUserId(userId);
        if (existingInfo != null) {
            companyInfo.setId(existingInfo.getId());
        }
        boolean saved = companyInfoService.saveOrUpdate(companyInfo);
        if (saved) {
            return Result.success(companyInfo);
        }
        return Result.error("保存失败");
    }

    /**
     * 获取企业信息
     * @return Result<CompanyInfo>
     */
    @GetMapping("/get")
    @Operation(summary = "获取企业信息", description = "获取当前登录企业的信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<CompanyInfo> get() {
        // 获取当前登录用户的ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        CompanyInfo companyInfo = companyInfoService.getByUserId(userId);
        if (companyInfo != null) {
            return Result.success(companyInfo);
        }
        return Result.error("企业信息不存在");
    }
}
