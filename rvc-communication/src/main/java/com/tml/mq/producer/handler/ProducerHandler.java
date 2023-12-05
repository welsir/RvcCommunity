package com.tml.mq.producer.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tml.constant.DetectionConstants.EXCHANGE_TOPICS_INFORM;

/**
 * @NAME: ProducerHandler
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Slf4j
@Component
public class ProducerHandler {

    @Resource
    RabbitTemplate rabbitTemplate;
//
    public void submit(Object submit,String type){
        rabbitTemplate.convertAndSend(EXCHANGE_TOPICS_INFORM, "inform" + type, JSON.toJSONString(submit));
    }
}