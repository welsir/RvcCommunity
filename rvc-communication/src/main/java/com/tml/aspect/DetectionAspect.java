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
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
                            detectionTaskDto.setType("image");
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
        log.info("=======Start===================================");
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



        // 1.创建CorrelationData
        CorrelationData cd = new CorrelationData();
        // 2.给Future添加ConfirmCallback
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                // 2.1.Future发生异常时的处理逻辑，基本不会触发
                log.error("send message fail", ex);
            }
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                // 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
                if(result.isAck()){ // result.isAck()，boolean类型，true代表ack回执，false 代表 nack回执
                    log.debug("发送消息成功，收到 ack!");
                }else{ // result.getReason()，String类型，返回nack时的异常描述
                    log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
                }
            }
        });
        rabbitTemplate.convertAndSend(DETECTION_EXCHANGE_NAME,"detection.topic.key",JSON.toJSONString(detectionTaskDto),cd);
    }
}
