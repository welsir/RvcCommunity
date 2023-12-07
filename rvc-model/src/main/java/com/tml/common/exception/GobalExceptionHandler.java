package com.tml.exception;

import com.tml.common.Result;
import com.tml.pojo.ResultCodeEnum;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
                result = new Result(resultCode.getCode(),
                        StringUtils.isEmpty(se.getMessage()) ? se.getMessage() : resultCode.getMsg());
            }
        }
        //参数错误
        else {
            result = new Result(ResultCodeEnum.SYSTEM_ERROR.getCode(), ex.getMessage());
        }
        logger.info("exception handle result:" + result);
        return result;
    }
}
