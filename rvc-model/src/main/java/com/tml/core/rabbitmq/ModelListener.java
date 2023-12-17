package com.tml.core.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.common.constant.RabbitMQconstant;
import com.tml.mapper.ModelMapper;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.DetectionStatusDTO;
import com.tml.pojo.DTO.DetectionTaskDTO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.channels.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 16:03
 */
@Component
public class ModelListener implements ListenerInterface{

    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    RabbitMQConfig config;
    @Resource
    ModelMapper modelMapper;

    public void sendMsgToMQ(DetectionTaskDTO task,String routingKey){
        String exchange = config.getPreCommand()+"."+config.getExchangeType();
        routingKey = config.getPreCommand()+"."+routingKey;
        rabbitTemplate.convertAndSend(exchange,routingKey, JSON.toJSONString(task));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.text"
    ))
    @Override
    public void receiveText(Message message){
        String messageBody = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        try {
            DetectionStatusDTO statusDTO = mapper.readValue(messageBody, DetectionStatusDTO.class);
            if(!"model".equals(statusDTO.getName())){
                return;
            }
            UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
            wrapper
                    .eq("id",statusDTO.getId())
                    .setSql("has_show="+statusDTO.getStatus());
            modelMapper.update(null,wrapper);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
