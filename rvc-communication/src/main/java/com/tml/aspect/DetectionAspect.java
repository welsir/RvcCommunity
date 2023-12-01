package com.tml.aspect;

import com.tml.annotation.Detection;
import com.tml.mq.producer.simplefactory.SimpleDetectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @NAME: DetectionAspect
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Component
@Aspect
@Slf4j
public class DetectionAspect {
    @Pointcut("@annotation(com.tml.annotation.Detection)")
    private void pt(){

    }

    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取带有 @Detection 注解的方法上的注解值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Detection annotation = method.getAnnotation(Detection.class);
        String value = annotation.value(); // 获取注解值

        com.tml.mq.producer.simplefactory.Detection detection = SimpleDetectionFactory.createDetection(value);
        Object[] args = joinPoint.getArgs();
        detection.submit(args);

        // 继续执行原方法
        return joinPoint.proceed();
    }

}