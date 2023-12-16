package com.tml.core.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.tml.core.factory.mqFactory.BaseMqProductorInterface;
import com.tml.pojo.DTO.DetectionTaskDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/15 8:57
 */
public class RabbitMqConsumer implements BaseMqProductorInterface<DetectionTaskDTO> {

    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    RabbitMQConfig config;

    @Override
    public void sendMsgToMq(DetectionTaskDTO task,String type) {
        String exchange = config.getPreCommand()+"."+config.getExchangeType();
        String routingKey = config.getPreCommand()+"."+type;
        rabbitTemplate.convertAndSend(exchange,routingKey, JSON.toJSONString(task));
    }
}
