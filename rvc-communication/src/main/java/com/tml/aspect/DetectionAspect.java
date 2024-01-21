package com.tml.aspect;


import com.alibaba.fastjson.JSON;
import com.tml.aspect.annotation.ContentDetection;
import com.tml.constant.enums.ContentDetectionEnum;
import com.tml.domain.dto.DetectionTaskDto;
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

import static com.tml.constant.DetectionConstants.*;


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

    public DetectionTaskDto setContentValue(Object[] args) throws IllegalAccessException {
        /**
         * 根据请求入参名来选择要审核的内容
         */
        DetectionTaskDto detectionTaskDto = new DetectionTaskDto();
        String contentValue = null;
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null) {
                    Class<?> argClass = arg.getClass();
                    Field[] fields = argClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        //入参名
                        if (DETECTION_TEXT_KEY.equals(field.getName())) {
                            contentValue = (String) field.get(arg);
                            detectionTaskDto.setContent(contentValue);
                            detectionTaskDto.setType("text");
                            break;
                        } else if (DETECTION_IMG_KEY.equals(field.getName())) {
                            contentValue = (String) field.get(arg);
                            detectionTaskDto.setContent(contentValue);
                            detectionTaskDto.setType("img");
                            break;
                        } else if (DETECTION_AUDIO_KEY.equals(field.getName())) {
                            contentValue = (String) field.get(arg);
                            detectionTaskDto.setContent(contentValue);
                            detectionTaskDto.setType("audio");
                            break;
                        }
                    }
                }

            }
        }
        return detectionTaskDto;
    }

    private void handleAfter(ProceedingJoinPoint joinPoint,Object ret) throws IllegalAccessException {
        /**
         * 获取审核内容的主键id
         * 获取被增强方法上的注解对象
         */
        Result res = (Result)(ret);
        String uuid = res.getData().toString();
        ContentDetection contentDetection = getContentDetection(joinPoint);
        //获取审核内容
        Object[] args = joinPoint.getArgs();
        DetectionTaskDto detectionTaskDto = setContentValue(args);
        //设置参数，发布任务
        detectionTaskDto.setId(uuid);
        detectionTaskDto.setRouterKey(contentDetection.routerKey());
        rabbitTemplate.convertAndSend(DETECTION_EXCHANGE_NAME,DETECTION_ROUTER_KEY,JSON.toJSONString(detectionTaskDto));
    }
}
