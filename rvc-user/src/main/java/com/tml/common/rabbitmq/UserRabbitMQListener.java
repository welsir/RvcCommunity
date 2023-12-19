package com.tml.common.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.exception.GlobalExceptionHandler;
import com.tml.exception.ServerException;
import com.tml.pojo.dto.DetectionStatusDTO;
import com.tml.pojo.dto.DetectionTaskDTO;
import com.tml.pojo.enums.LabelEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Component
public class UserRabbitMQListener implements ListenerInterface {
    private static final Logger logger = LoggerFactory.getLogger(UserRabbitMQListener.class);

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    RabbitMQConfig config;

    @Override
    public void sendMsgToMQ(DetectionTaskDTO task, String type) {
        String exchange = config.getPreCommand()+"."+config.getExchangeType();
        type = config.getPreCommand()+"."+type;
        rabbitTemplate.convertAndSend(exchange, type, JSON.toJSONString(task));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.text"
    ))
    @Override
    public void receiveText(Message message){
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        try {
            DetectionStatusDTO detectionTaskDto = mapper.readValue(content, DetectionStatusDTO.class);
            if(detectionTaskDto.getLabels().equals(LabelEnums.NON_LABEL)){

            }

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new ServerException("500", e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.image"
    ))
    public void receiveImage(Message message){
        String messageBody = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        try {
            DetectionStatusDTO detectionTaskDto = mapper.readValue(messageBody, DetectionStatusDTO.class);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new ServerException("500", e.getMessage());
        }
    }
}
