package com.tml.common.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.exception.GlobalExceptionHandler;
import com.tml.exception.ServerException;
import com.tml.mq.ReceiveHandler;
import com.tml.pojo.dto.DetectionStatusDTO;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.dto.DetectionTaskDTO;
import com.tml.pojo.enums.LabelEnums;
import com.tml.pojo.vo.UserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static com.tml.config.DetectionConfig.USER_NICKNAME;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Component
public class UserRabbitMQListener extends ReceiveHandler {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void process(DetectionStatusDto detectionTaskDto) {
        String uid = detectionTaskDto.getId();
//        switch (detectionTaskDto.getName()){
//            case USER_NICKNAME:
//        }
    }

    public void textSubmit(DetectionTaskDTO task){
        rabbitTemplate.convertAndSend("detection.topic", "detection.text", JSON.toJSONString(task));
    }

    public void imageSubmit(DetectionTaskDTO task){
        rabbitTemplate.convertAndSend("detection.topic", "detection.image", JSON.toJSONString(task));
    }
}
