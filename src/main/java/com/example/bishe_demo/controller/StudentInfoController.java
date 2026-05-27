package com.example.bishe_demo.controller;

import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.entity.StudentInfo;
import com.example.bishe_demo.service.StudentInfoService;
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
 * 学生信息扩展表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Tag(name = "学生信息接口")
@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class StudentInfoController {
    @Autowired
    private StudentInfoService studentInfoService;

    /**
     * 保存或更新学生信息
     * @param studentInfo 学生信息
     * @return Result<StudentInfo>
     */
    @PostMapping("/save")
    @Operation(summary = "保存或更新学生信息", description = "学生保存或更新自己的信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<StudentInfo> save(@RequestBody StudentInfo studentInfo) {
        // 获取当前登录用户的ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        studentInfo.setUserId(userId);
        // 检查是否已存在学生信息
        StudentInfo existingInfo = studentInfoService.getByUserId(userId);
        if (existingInfo != null) {
            studentInfo.setId(existingInfo.getId());
        }
        boolean saved = studentInfoService.saveOrUpdate(studentInfo);
        if (saved) {
            return Result.success(studentInfo);
        }
        return Result.error("保存失败");
    }

    /**
     * 获取学生信息
     * @return Result<StudentInfo>
     */
    @GetMapping("/get")
    @Operation(summary = "获取学生信息", description = "获取当前登录学生的信息")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<StudentInfo> get() {
        // 获取当前登录用户的ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        StudentInfo studentInfo = studentInfoService.getByUserId(userId);
        if (studentInfo != null) {
            return Result.success(studentInfo);
        }
        return Result.error("学生信息不存在");
    }
}
