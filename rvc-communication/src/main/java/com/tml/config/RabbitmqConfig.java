package com.tml.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tml.constant.DetectionConstants.*;


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
    @Bean(RES_EXCHANGE_NAME)
    public Exchange RES_EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.fanoutExchange(RES_EXCHANGE_NAME).durable(true).build();
    }

//    //声明交换机
//    @Bean(DETECTION_EXCHANGE_NAME)
//    public Exchange DETECTION_EXCHANGE_TOPICS_INFORM(){
//        //durable(true) 持久化，mq重启之后交换机还在
//        return ExchangeBuilder.topicExchange(DETECTION_EXCHANGE_NAME).durable(true).build();
//    }

    //声明QUEUE_INFORM_TEXT 队列
//    @Bean(TEXT_QUEUE_NAME )
//    public Queue QUEUE_INFORM_TEXT(){
//        return new Queue(TEXT_QUEUE_NAME );
//    }
//
//
//    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
//    @Bean
//    public Binding BINDING_QUEUE_INFORM_TEXT(@Qualifier(TEXT_QUEUE_NAME ) Queue queue,
//                                              @Qualifier(RES_EXCHANGE_NAME) Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(TEXT_ROUTER_KEY).noargs();
//    }
//
//
//
//    //声明QUEUE_INFORM_IMAGE 队列
//    @Bean(IMAGE_QUEUE_NAME )
//    public Queue QUEUE_INFORM_IMAGE(){
//        return new Queue(IMAGE_QUEUE_NAME );
//    }
//
//
//    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
//    @Bean
//    public Binding BINDING_QUEUE_INFORM_IMAGE (@Qualifier(IMAGE_QUEUE_NAME  ) Queue queue,
//                                             @Qualifier(RES_EXCHANGE_NAME) Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(IMAGE_ROUTER_KEY ).noargs();
//    }
//
//
//
//    //声明QUEUE_INFORM_AUDIO 队列
//    @Bean(AUDIO_QUEUE_NAME)
//    public Queue QUEUE_INFORM_AUDIO(){
//        return new Queue(AUDIO_QUEUE_NAME );
//    }
//
//
//    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
//    @Bean
//    public Binding BINDING_QUEUE_INFORM_AUDIO(@Qualifier(AUDIO_QUEUE_NAME) Queue queue,
//                                             @Qualifier(RES_EXCHANGE_NAME) Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(AUDIO_ROUTER_KEY).noargs();
//    }


}