package com.tml.common.exception;

import com.tml.common.Result;
import com.tml.common.log.AbstractLogger;
import com.tml.pojo.ResultCodeEnum;
import io.github.exception.handler.AbstractExceptionHandler;
import io.github.exception.handler.AssistantExceptionHandlerCondition;
import io.github.exception.handler.annotation.AssistantControllerAdvice;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
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
@AssistantControllerAdvice("rvc-model-service-exceptionHandler")
@Conditional(AssistantExceptionHandlerCondition.class)
public class GobalExceptionHandler extends AbstractExceptionHandler {

    @Resource
    AbstractLogger logger;

    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public Result handleException(HttpServletRequest request,
                                  Exception ex) {
        logger.error("Handle Exception Request Url:%s,Exception:%s", request.getRequestURL(), ex);
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

    @Override
    public int getOrder() {
        return 1;
    }
}
