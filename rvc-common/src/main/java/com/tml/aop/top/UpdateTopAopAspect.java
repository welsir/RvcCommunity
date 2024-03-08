package com.tml.aop.top;

import io.github.common.web.Result;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Date 2024/2/29
 * @Author xiaochun
 */
@Aspect
@Component
public class UpdateTopAopAspect {
    @Pointcut("@annotation(com.tml.annotation.rankingAspect.UpdateTop)")
    public void annotatedMethod(){
    }

    @AfterReturning(pointcut = "annotatedMethod()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Result result) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();

        System.out.println(methodName);
    }
}
