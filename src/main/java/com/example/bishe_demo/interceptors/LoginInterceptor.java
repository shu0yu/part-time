package com.example.bishe_demo.interceptors;


import com.example.bishe_demo.annotation.RequiredRole;
import com.example.bishe_demo.utils.JwtUtil;
import com.example.bishe_demo.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        // 验证token
        try {
            if (token == null || token.isEmpty()) {
                // token为空，直接放行（由WebConfig中的excludePathPatterns控制哪些路径需要登录）
                return true;
            }
            
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 存储业务信息到线程变量
            ThreadLocalUtil.set(claims);
            
            // 检查角色权限
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 检查方法上的注解
                RequiredRole requiredRole = handlerMethod.getMethodAnnotation(RequiredRole.class);
                // 如果方法上没有注解，检查类上的注解
                if (requiredRole == null) {
                    requiredRole = handlerMethod.getBeanType().getAnnotation(RequiredRole.class);
                }
                // 如果有注解，检查角色权限
                if (requiredRole != null) {
                    String[] allowedRoles = requiredRole.value();
                    java.util.List<String> userRoles = (java.util.List<String>) claims.get("roles");
                    boolean hasPermission = false;
                    for (String role : allowedRoles) {
                        if (userRoles != null && userRoles.contains(role)) {
                            hasPermission = true;
                            break;
                        }
                    }
                    if (!hasPermission) {
                        // 权限不足，http响应状态码为403
                        response.setStatus(403);
                        return false;
                    }
                }
            }
            
            // 放行拦截
            return true;
        } catch (Exception e) {
            // 登录信息异常，http响应状态码为401
            response.setStatus(401);
            // 拦截请求
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal业务数据
        ThreadLocalUtil.remove();
    }
}
