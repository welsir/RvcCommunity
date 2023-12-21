package com.tml.exception;

import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * @Date 2023/5/15
 * @Author xiaochun
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
    * 自定义异常类拦截器
    * * */
    @ExceptionHandler(ServerException.class)
    public Result handleException(ServerException e){
        logger.error("自定义错误：" + e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /*
    * HSR 303 校验 异常拦截器
    * * */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result handleException(MissingServletRequestPartException e){
        logger.error("303校验：" + "需要参数" + e.getRequestPartName());
        return Result.error("303", "需要参数" + e.getRequestPartName());
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("303校验：" + ex.getConstraintViolations().iterator().next().getMessage());
        return Result.error("303", ex.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : errors) {
            logger.error("303校验：" + error.getObjectName() + "参数错误 " + error.getDefaultMessage());
        }
        return Result.error("303", errors.get(0).getDefaultMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result handleException(MissingRequestHeaderException e){
        logger.error("请求头缺失：" + e.getHeaderName());
        return Result.error("303", "缺失请求头" + e.getHeaderName());
    }

    @ExceptionHandler(Exception.class)
    public Result handleBindException(Exception ex) {
        logger.error("未定义错误：" + ex.getMessage());
        ex.printStackTrace();
        return Result.error("400", ex.getMessage());
    }
}
