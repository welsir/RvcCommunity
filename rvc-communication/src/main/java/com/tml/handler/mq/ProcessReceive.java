package com.tml.handler.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tml.mq.ReceiveHandler;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.designpattern.strategy.DetectionProcessStrategy;
import com.tml.designpattern.strategy.impl.CommentProcessStrategy;
import com.tml.designpattern.strategy.impl.CoverProcessStrategy;
import com.tml.designpattern.strategy.impl.PostProcessStrategy;
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
import java.util.Objects;

import static com.tml.constant.DetectionConstants.*;
import static com.tml.constant.enums.ContentDetectionEnum.*;

/**
 * @NAME: ProcessReceive
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/19
 */
@Component
@Slf4j
public class ProcessReceive  {

    private final Map<String, DetectionProcessStrategy> strategyMap = new HashMap<>();

    @Autowired
    public ProcessReceive(CoverProcessStrategy coverProcessStrategy, CommentProcessStrategy commentProcessStrategy, PostProcessStrategy postProcessStrategy) {
        strategyMap.put(POST_COVER.getFullName() ,coverProcessStrategy);
        strategyMap.put(COMMENT.getFullName(),commentProcessStrategy);
        strategyMap.put(POST_CONTENT.getFullName(),postProcessStrategy);
    }


    /**
     * 评论
     * @param message
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DETECTION_RES_COMMENT_QUEUE),
            exchange = @Exchange(name = DETECTION_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = DETECTION_RES_COMMENT_KEY
    ))
    public void comment(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(COMMENT.getFullName());
        detectionProcessStrategy.process(detectionTaskDto);
    }


    /**
     * 封面
     * @param message
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DETECTION_RES_COVER_QUEUE),
            exchange = @Exchange(name = DETECTION_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = DETECTION_RES_COVER_KEY
    ))
    public void cover(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(POST_COVER.getFullName());
        detectionProcessStrategy.process(detectionTaskDto);
    }
}