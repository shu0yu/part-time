package com.example.bishe_demo.config;

import com.example.bishe_demo.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:5174", "http://127.0.0.1:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*").allowCredentials(true).maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置放行资源
        // 无需拦截的接口集合
        List<String> ignorePath = new ArrayList<>();
        // knife4j(swagger)
        ignorePath.add("/swagger-resources/**");
        ignorePath.add("/doc.html");
        ignorePath.add("/v3/**");
        ignorePath.add("/webjars/**");
        ignorePath.add("/static/**");
        ignorePath.add("/templates/**");
        ignorePath.add("/error");
        // 登录页面
        ignorePath.add("/user/login");
        // 公开接口
        ignorePath.add("/job/list");
        registry.addInterceptor(loginInterceptor).excludePathPatterns(ignorePath);
    }
}
