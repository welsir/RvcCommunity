package com.tml.common.exception;

import com.tml.common.Result;
import com.tml.common.log.AbstractLogger;
import com.tml.pojo.ResultCodeEnum;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 13:38
 */
@ControllerAdvice
@ResponseBody
public class GobalExceptionHandler {

    @Resource
    AbstractLogger logger;

    @ExceptionHandler(BaseException.class)
    public Result handleException(HttpServletRequest request,
                                  Exception ex) {
        logger.error("Handle Exception Request Url:{},Exception:{}", request.getRequestURL(), ex);
        Result result;
        //系统异常
        if (ex instanceof BaseException) {
            BaseException se = (BaseException) ex;
            ResultCodeEnum resultCode = se.getResultCode();
            if (resultCode == null) {
                result = Result.fail(se.getMessage());
            } else {
                result = Result.fail(resultCode.getCode(),
                        StringUtils.isEmpty(se.getMessage()) ? resultCode.getMsg():se.getMessage());
            }
        }
        //参数错误
        else {
            result = new Result(ResultCodeEnum.SYSTEM_ERROR.getCode(), ex.getMessage());
        }
        logger.info("exception handle result:" + result);
        return result;
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result bindExceptionHandler(BindException e){
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return Result.fail(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result constraintViolationException(ConstraintViolationException e){
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return Result.fail(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return Result.fail(message);
    }

}
