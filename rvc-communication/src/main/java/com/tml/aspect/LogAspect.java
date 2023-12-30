package com.tml.aspect;

import com.alibaba.fastjson.JSON;
import com.tml.aspect.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 日志打印
 */

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(com.tml.aspect.annotation.SystemLog)")
    public void pt(){

    }

    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis(); // 获取程序开始时间
        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
//            handleAfter(ret);
        } finally {
            long endTime = System.currentTimeMillis(); // 获取程序结束时间
            long totalTime = endTime - startTime; // 计算总运行时间
            log.info("run time :" + totalTime + "ms");
            log.info("=======End==================================="+System.lineSeparator());
        }

        return ret;
    }

    private void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        Enumeration<String> token = request.getHeaders("token");

    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        SystemLog annotation = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return annotation;
    }

    private void handleAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}",JSON.toJSONString(ret) );
    }
}