package com.tml.aspect;


import com.tml.annotation.ContentDetection;
import com.tml.utils.Uuid;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


/**
 * @NAME: DetectionAspect
 * @USER: yuech
 * @Description:审核切片类
 * @DATE: 2023/12/18
 */
@Component
@Aspect
@Slf4j
public class DetectionAspect {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.tml.annotation.ContentDetection)")
    public void pt2(){}


    @Around("pt2()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            handleBefore();
            ret = joinPoint.proceed();
            handleAfter(joinPoint);
        } finally {
            //打印并换行
            log.info("=======End==================================="+System.lineSeparator());
        }

        return ret;
    }

    private ContentDetection getContentDetection(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        ContentDetection annotation = methodSignature.getMethod().getAnnotation(ContentDetection.class);
        return annotation;
    }
    private void handleBefore() {
        log.info("=======Start===================================");

    }
    private void handleAfter(ProceedingJoinPoint joinPoint) {
        //获取被增强方法上的注解对象
        ContentDetection contentDetection = getContentDetection(joinPoint);
        String name = contentDetection.businessName();
        String exchangeName = contentDetection.exchangeName();
        String uuid = Uuid.getUuid();
        Object[] args = joinPoint.getArgs();



//        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
//        .id(uuid)
//        .content(commentDto.getContent())
//        .name("comment.text")
//        .build();
    }
}