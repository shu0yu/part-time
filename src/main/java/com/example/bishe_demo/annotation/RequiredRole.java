package com.example.bishe_demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限验证注解
 * 用于标记需要特定角色才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole {
    /**
     * 允许访问的角色类型数组
     * ADMIN - 系统管理员，拥有最高权限
     * COMPANY - 企业用户，可发布兼职岗位
     * STUDENT - 学生用户，可申请兼职岗位
     */
    String[] value();
}
