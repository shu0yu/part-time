package com.example.bishe_demo.common;

import com.example.bishe_demo.constant.SystemConstant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一响应结果封装类
 * @param <T> 响应数据的类型
 */
public class Result<T> {
    
    @Schema(description = "业务状态码 0：成功  1: 失败")
    private int code;
    
    @Schema(description = "提示信息")
    private String message;
    
    @Schema(description = "返回数据")
    private T data;

    /**
     * 构造函数
     * @param code 业务状态码
     * @param message 提示信息
     * @param data 响应数据
     */
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 操作成功返回响应结果（带响应数据）
     * @param data 响应数据
     * @param <E> 响应数据类型
     * @return 成功的响应结果
     */
    public static <E> Result<E> success(E data) {
        return new Result<>(SystemConstant.RESULT_CODE_SUCCESS, "操作成功", data);
    }

    /**
     * 操作成功返回响应结果（不带响应数据）
     * @param <E> 响应数据类型
     * @return 成功的响应结果
     */
    public static <E> Result<E> success() {
        return new Result<>(SystemConstant.RESULT_CODE_SUCCESS, "操作成功", null);
    }

    /**
     * 操作失败返回响应结果
     * @param message 错误提示信息
     * @param <E> 响应数据类型
     * @return 失败的响应结果
     */
    public static <E> Result<E> error(String message) {
        return new Result<>(SystemConstant.RESULT_CODE_ERROR, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}