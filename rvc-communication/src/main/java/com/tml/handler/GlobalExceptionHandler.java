package com.tml.handler;


import com.tml.constant.enums.AppHttpCodeEnum;
import com.tml.handler.exception.SystemException;
import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public Result systemExceptionHandler(SystemException e){
        //打印异常信息
        log.error("出现了异常！ {}",e);
        //从异常对象中获取提示信息封装返回
        return Result.error(e.getCode(),e.getMsg());
    }

    /**
     * 处理所有RequestBody注解参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("出现了异常！ {}",e);
        return Result.error("303",e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

//    @ExceptionHandler(ConstraintViolationException.class)
//    public Result<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
//        ex.printStackTrace();
//        return Result.error("303", ex.getConstraintViolations().iterator().next().getMessage());
//    }


    /**
     * 处理所有RequestParam注解数据验证异常
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.warn("必填校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        return Result.error("303",ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }






    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e){
        //打印异常信息
        log.error("出现了异常！ {}",e);
        //从异常对象中获取提示信息封装返回
        return Result.error(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
