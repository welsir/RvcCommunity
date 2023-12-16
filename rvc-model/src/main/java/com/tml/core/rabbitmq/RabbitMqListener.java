//package com.tml.core.rabbitmq;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tml.common.log.AbstractLogger;
//import com.tml.core.factory.mqFactory.BaseMqConsumerInterface;
//import com.tml.pojo.DTO.DetectionStatusDTO;
//import org.springframework.context.ApplicationContext;
//import org.springframework.amqp.core.ExchangeTypes;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Map;
//
///**
// * @Description
// * @Author welsir
// * @Date 2023/12/15 8:23
// */
//@Component
//public class RabbitMqListener implements BaseMqConsumerInterface {
//
//    @Resource
//    AbstractLogger logger;
//    @Resource
//    ApplicationContext applicationContext;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(),
//            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
//            key = "res.text"
//    ))
//    @Override
//    public Object receiveText(Message message) {
//        String messageBody = new String(message.getBody());
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.readValue(messageBody, DetectionStatusDTO.class);
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(),
//            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
//            key = "res.image"
//    ))
//    @Override
//    public Object receiveImage(Message message) {
//        String messageBody = new String(message.getBody());
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.readValue(messageBody, DetectionStatusDTO.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(),
//            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
//            key = "res.audio"
//    ))
//    @Override
//    public Object receiveAudio(Message message) {
//        String messageBody = new String(message.getBody());
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.readValue(messageBody, DetectionStatusDTO.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public <T> BaseMapper<T> getMapperByEntityType(Class<T> entityType) {
//        // 获取所有 Mapper Bean
//        Map<String, BaseMapper> mappers = applicationContext.getBeansOfType(BaseMapper.class);
//
//        for (BaseMapper mapper : mappers.values()) {
//            // 获取 Mapper 接口的泛型类型
//            Type[] genericInterfaces = mapper.getClass().getGenericInterfaces();
//            for (Type genericInterface : genericInterfaces) {
//                if (genericInterface instanceof ParameterizedType) {
//                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
//                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//                    if (actualTypeArguments.length > 0 && actualTypeArguments[0].equals(entityType)) {
//                        // 找到匹配的 Mapper
//                        return mapper;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//}
