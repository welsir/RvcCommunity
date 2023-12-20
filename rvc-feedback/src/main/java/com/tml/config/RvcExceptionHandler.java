package com.tml.config;

import com.tml.exception.RvcSQLException;
import io.github.common.logger.CommonLogger;
import io.github.common.web.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RvcExceptionHandler {

    @Resource
    CommonLogger logger;

    @ResponseBody
    @ExceptionHandler(value = RvcSQLException.class)
    public Result SqlErrorHandler(HttpServletRequest httpServletRequest, Exception e){
        logger.error("数据库异常:{}",e.getMessage());
        return Result.error("503",e.getMessage());

    }

}
