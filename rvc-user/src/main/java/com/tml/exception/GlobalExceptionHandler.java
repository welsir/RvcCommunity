package com.tml.exception;

import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        logger.error("数据错误：" + e.getMessage());
        e.printStackTrace();
        return Result.error(e.getCode(), e.getMessage());
    }

    /*
    * HSR 303 校验 异常拦截器
    * * */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public Result handleException(ConstraintViolationException e){
//        List<ObjectError> errors = e.getBindingResult().getAllErrors();
//        for (ObjectError error : errors) {
//            logger.error("参数错误：" + error.getObjectName() + "参数错误 " + error.getDefaultMessage());
//        }
//        return Result.error("400", errors.get(0).getDefaultMessage());
//    }
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        ex.printStackTrace();
        return Result.error("303", ex.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : errors) {
            logger.error("参数错误：" + error.getObjectName() + "参数错误 " + error.getDefaultMessage());
        }
        ex.printStackTrace();
        return Result.error("303", errors.get(0).getDefaultMessage());
    }
}
