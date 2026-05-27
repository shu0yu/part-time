package com.example.bishe_demo.constant;

/**
 * 系统常量类
 * 用于定义系统中使用的常量，避免魔法值
 */
public class SystemConstant {

    private SystemConstant() {
    }

    /**
     * 结果状态码 - 成功
     */
    public static final int RESULT_CODE_SUCCESS = 0;

    /**
     * 结果状态码 - 失败
     */
    public static final int RESULT_CODE_ERROR = 1;

    /**
     * 删除状态 - 未删除
     */
    public static final byte NOT_DELETED = 0;

    /**
     * 删除状态 - 已删除
     */
    public static final byte DELETED = 1;

    /**
     * 用户状态 - 禁用
     */
    public static final byte USER_STATUS_DISABLED = 0;

    /**
     * 用户状态 - 正常
     */
    public static final byte USER_STATUS_ENABLED = 1;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认当前页码
     */
    public static final int DEFAULT_CURRENT_PAGE = 1;
}
