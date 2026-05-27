package com.example.bishe_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bishe_demo.common.PageResultBean;
import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.constant.SystemConstant;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.service.UserService;
import com.example.bishe_demo.utils.JwtUtil;
import com.example.bishe_demo.utils.Md5Util;
import com.example.bishe_demo.utils.UserIdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micrometer.common.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-01-15
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    /**
     * 构造函数注入依赖
     * @param userService 用户服务
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户列表分页查询
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param name 用户姓名（模糊查询）
     * @return 用户分页结果
     */
    @GetMapping("/pageList")
    @Operation(summary = "用户列表分页查询", description = "根据页码、页大小、用户名模糊查询用户列表")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "pageSize", required = true, description = "每页显示的条数"),
            @Parameter(name = "currentPage", required = true, description = "要查询的页"),
            @Parameter(name = "name", description = "用户姓名", required = false)
    })
    public Result<PageResultBean<User>> pageList(@RequestParam int pageSize, @RequestParam int currentPage, 
                                                   @Nullable @RequestParam String name) {
        IPage<User> page = userService.pageList(pageSize, currentPage, name);
        if (page == null) {
            return Result.error("查询失败");
        }
        return Result.success(PageResultBean.getInstance(page.getTotal(), page.getRecords()));
    }

    /**
     * 用户登录
     * @param user 登录信息（用户名和密码）
     * @return 登录成功返回token，失败返回错误信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @Parameters({
            @Parameter(name = "username", description = "登录名", required = true, schema = @Schema(type = "string")),
            @Parameter(name = "password", description = "密码", required = true, schema = @Schema(type = "string"))
    })
    public Result<String> login(@RequestBody User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty() 
                || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("用户名密码不能为空");
        }
        
        User existUser = userService.findByUsername(user.getUsername());
        if (existUser == null) {
            return Result.error("用户名不存在");
        }
        
        if (existUser.getStatus() != null && existUser.getStatus() == SystemConstant.USER_STATUS_DISABLED) {
            return Result.error("账号已被禁用，请联系管理员");
        }
        
        if (Md5Util.getMD5String(user.getPassword()).equals(existUser.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", existUser.getId());
            claims.put("realName", existUser.getRealName());
            claims.put("username", existUser.getUsername());
            claims.put("phone", existUser.getPhone());
            
            List<String> roles = userService.getUserRoles(existUser.getId());
            claims.put("roles", roles);
            
            String token = JwtUtil.getToken(claims);
            return Result.success(token);
        }
        
        return Result.error("密码错误");
    }

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息（包含角色）
     */
    @GetMapping("/currentUser")
    @Operation(summary = "获取当前登录用户信息")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    public Result<Map<String, Object>> getCurrentUser() {
        Long id = UserIdUtil.getCurrentUserId();
        if (id == null) {
            return Result.error("用户状态异常");
        }
        
        User user = userService.getUserById(id);
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("status", user.getStatus());
            
            List<String> roles = userService.getUserRoles(user.getId());
            userInfo.put("roles", roles);
            
            return Result.success(userInfo);
        }
        
        return Result.error("用户状态异常");
    }
}

