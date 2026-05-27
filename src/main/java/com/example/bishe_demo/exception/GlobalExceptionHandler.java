package com.example.bishe_demo.exception;

import com.example.bishe_demo.common.Result;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理系统中的异常情况
 */
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e 业务异常对象
     * @return 统一的错误响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理所有其他异常
     * @param e 异常对象
     * @return 统一的错误响应结果
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        logger.error("系统异常:", e);
        return Result.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
