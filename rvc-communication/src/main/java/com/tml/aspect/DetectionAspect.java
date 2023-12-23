package com.tml.aspect;


import com.alibaba.fastjson.JSON;
import com.tml.aspect.annotation.ContentDetection;
import com.tml.enums.ContentDetectionEnum;
import com.tml.pojo.dto.DetectionTaskDto;
import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

import static com.tml.constant.DetectionConstants.DETECTION_ROUTER_KEY_HEADER;


/**
 * @NAME: DetectionAspect
 * @USER: yuech
 * @Description:内容审核
 * @DATE: 2023/12/18
 */
@Component
@Aspect
@Slf4j
public class DetectionAspect {

    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.tml.aspect.annotation.ContentDetection)")
    public void pt2(){}


    @Around("pt2()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            handleBefore();
            ret = joinPoint.proceed();
            handleAfter(joinPoint,ret);
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
    private void handleAfter(ProceedingJoinPoint joinPoint,Object ret) throws IllegalAccessException {

        Result res = (Result)(ret);

        //获取被增强方法上的注解对象
        ContentDetection contentDetection = getContentDetection(joinPoint);
        String exchangeName = contentDetection.exchangeName();
        String uuid = res.getData().toString();
        ContentDetectionEnum type = contentDetection.type();
        Object[] args = joinPoint.getArgs();

        String contentValue = null;
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null) {
                    Class<?> argClass = arg.getClass();
                    Field[] fields = argClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        //入参名
                        if ("content".equals(field.getName())) {
                            contentValue = (String) field.get(arg);
                            break;
                        } else if ("coverUrl".equals(field.getName())) {
                            contentValue = (String) field.get(arg);
                            break;
                        }
                    }
                }
                DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                        .id(uuid)
                        .content(contentValue)
                        .name(type.getName()+":" + type.getType())
                        .build();
                //在此处 上传任务到mq
                rabbitTemplate.convertAndSend(exchangeName, DETECTION_ROUTER_KEY_HEADER + type.getType(), JSON.toJSONString(textDetectionTaskDto));

            }
        }
    }



}