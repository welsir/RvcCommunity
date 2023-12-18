package com.tml.mq.handler;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.strategy.DetectionProcessStrategy;

import com.tml.strategy.impl.CommentProcessStrategy;
import com.tml.strategy.impl.CoverProcessStrategy;

import com.tml.strategy.impl.PostProcessStrategy;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
    public ReceiveHandler(CoverProcessStrategy coverProcessStrategy, CommentProcessStrategy commentProcessStrategy, PostProcessStrategy postProcessStrategy) {
        strategyMap.put("post_cover", coverProcessStrategy);
        strategyMap.put("comment.text",commentProcessStrategy);
        strategyMap.put("post.text",postProcessStrategy);
    }

//    监听text队列

    @RabbitListener(bindings = @QueueBinding(
                value = @Queue(),
//            value = @Queue(name = "res.text"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.text"
    ))
    public void receive_text(Message message) throws Exception {

        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

        System.out.println(detectionTaskDto);


////处理逻辑  更新数据库
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
        //如果没有的话就是其他服务的处理  直接放行
        if(Objects.isNull(detectionProcessStrategy)){
            return;
        }
////处理逻辑  更新数据库
        detectionProcessStrategy.process(detectionTaskDto);

    }



    //监听 image 队列

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
//            value = @Queue(name = "res.image"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.image"
    ))
    public void receive_image(Message message) throws Exception {
        System.out.println(LocalDate.now());

        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);
////处理逻辑  更新数据库
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
        //如果没有的话就是其他服务的处理  直接放行
        if(Objects.isNull(detectionProcessStrategy)){
            return;
        }

////处理逻辑  更新数据库
        detectionProcessStrategy.process(detectionTaskDto);

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
//            value = @Queue(name = "res.audio"),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.audio"
    ))
    public void receive_audio(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

//        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
////处理逻辑  更新数据库
//        detectionProcessStrategy.process(detectionTaskDto);

    }
}