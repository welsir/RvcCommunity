
package com.tml.aop;

import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.RvcLevelTask;
import com.tml.handler.mq.producer.ProcessHandler;
import com.tml.mapper.RvcLevelTaskMapper;
import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @NAME: GradeSystemAspect
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/21
 */
@Slf4j
@Component
@Aspect
public class GradeSystemAspect {

    @Autowired
    private ProcessHandler processHandler;

    @Pointcut("@annotation(com.tml.aop.annotation.GradeSystem)")
    public void pt(){

    }

    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret = joinPoint.proceed();
        handleAfter(ret);
        return ret;
    }

    public void handleAfter(Object ret){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Result res = (Result)(ret);
        if ("200".equals(res.getCode())){

            /// TODO: 2024/2/21 开启异步线程提交任务
            log.info("开启异步线程提交任务");
            String url = String.valueOf(request.getRequestURL());
            String[] parts = url.split("api");
            String path = parts[parts.length - 1];
            /// TODO: 2024/2/21 获取用户uid
            String uid = "33231313445";
            processHandler.sendTask(new MqConsumerTaskDto(path,uid));
        }
    }
}