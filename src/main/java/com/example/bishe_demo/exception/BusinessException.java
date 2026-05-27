package com.example.bishe_demo.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {

    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
