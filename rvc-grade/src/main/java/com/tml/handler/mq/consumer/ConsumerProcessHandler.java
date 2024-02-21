package com.tml.handler.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.RvcLevelTask;
import com.tml.mapper.RvcLevelTaskMapper;
import org.aspectj.lang.annotation.Around;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;


/**
 * @NAME: ConsumerProcessHandler
 * @USER: yuech
 * @Description:消费者处理消息
 * @DATE: 2024/2/21
 */
@Component
public class ConsumerProcessHandler {

    @Resource
    private RvcLevelTaskMapper rvcLevelTaskMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "rvc.grade.consumer.queue"),
            exchange = @Exchange(name = "rvc.grade.consumer",type = ExchangeTypes.TOPIC),
            key = "rvc.grade.consumer.key"
    ))
    public void process(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        MqConsumerTaskDto mqConsumerTaskDto = objectMapper.readValue(content, MqConsumerTaskDto.class);

        /// TODO: 2024/2/21 处理经验值增加的业务
        /**
         * 1、从数据库获取任务信息   去规则代码库解析
         * 2、从redis进行校验
         */
        RvcLevelTask task = rvcLevelTaskMapper.getOne(new QueryWrapper<RvcLevelTask>().eq("task_url", mqConsumerTaskDto.getPath()));
        JSONObject jsonObject = JSON.parseObject(task.getRule());
        System.out.println(jsonObject.get("1"));

    }
}