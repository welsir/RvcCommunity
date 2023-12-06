package com.tml.mq.producer.handler;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.tml.mapper.CoverMapper;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.strategy.DetectionProcessStrategy;

import com.tml.strategy.impl.CommentProcessStrategy;
import com.tml.strategy.impl.CoverProcessStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;





/**
 * @ClassName ReceiveHandler
 * @Description TODO
 * @Author yy
 * @Date 2019/12/17 13:02
 * @Version 1.0
 */
@Component
@Slf4j
public class ReceiveHandler {

    private final Map<String, DetectionProcessStrategy> strategyMap = new HashMap<>();

    @Autowired
    public ReceiveHandler(CoverProcessStrategy coverProcessStrategy, CommentProcessStrategy commentProcessStrategy) {
        strategyMap.put("post_cover", coverProcessStrategy);
        strategyMap.put("comment.text",commentProcessStrategy);
    }

//    监听text队列
//    @RabbitListener(queues = {QUEUE_STATUS_TEXT})
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "res.text"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.TOPIC),
            key = "res.text"
    ))
    public void receive_text(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

////处理逻辑  更新数据库
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
////处理逻辑  更新数据库
        detectionProcessStrategy.process(detectionTaskDto);
    }



    //监听 image 队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "res.image"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.TOPIC),
            key = "res.image"
    ))
    public void receive_image(Message message) throws Exception {

        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);
////处理逻辑  更新数据库
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
////处理逻辑  更新数据库
        detectionProcessStrategy.process(detectionTaskDto);

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "res.audio"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.TOPIC),
            key = "res.audio"
    ))
    public void receive_audio(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);
        System.out.println(detectionTaskDto);
//        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
////处理逻辑  更新数据库
//        detectionProcessStrategy.process(detectionTaskDto);

    }
}