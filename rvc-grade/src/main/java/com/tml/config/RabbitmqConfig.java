package com.tml.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @ClassName RabbitmqConfig
 * @Description TODO
 * @Author yy
 * @Date 2019/12/17 12:35
 * @Version 1.0
 */
@Configuration
public class RabbitmqConfig {

    //声明交换机
    @Bean("rvc.grade.consumer")
    public Exchange DETECTION_EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange("rvc.grade.consumer").durable(true).build();
    }



}