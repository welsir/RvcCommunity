package com.tml.mq.producer.handler;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.tml.pojo.dto.DetectionStatusDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


import static com.tml.constant.DetectionConstants.*;


/**
 * @ClassName ReceiveHandler
 * @Description TODO
 * @Author yy
 * @Date 2019/12/17 13:02
 * @Version 1.0
 */
@Component
public class ReceiveHandler {

    //监听text队列
    @RabbitListener(queues = {QUEUE_STATUS_TEXT})
    public void receive_text(Message message) throws Exception {

        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

//处理逻辑  更新数据库
        System.out.println(detectionTaskDto);

    }



    //监听 image 队列
    @RabbitListener(queues = {QUEUE_STATUS_IMAGE})
    public void receive_image(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

//处理逻辑  更新数据库
        System.out.println(detectionTaskDto);

    }


    @RabbitListener(queues = {QUEUE_STATUS_AUDIO})
    public void receive_audio(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        DetectionStatusDto detectionTaskDto = objectMapper.readValue(content, DetectionStatusDto.class);

//处理逻辑  更新数据库
        System.out.println(detectionTaskDto);

    }

}