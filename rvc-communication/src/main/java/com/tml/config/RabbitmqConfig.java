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
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }
 
    //声明QUEUE_INFORM_TEXT 队列
    @Bean(QUEUE_INFORM_TEXT )
    public Queue QUEUE_INFORM_TEXT(){
        return new Queue(QUEUE_INFORM_TEXT );
    }
//
//
    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_QUEUE_INFORM_TEXT(@Qualifier(QUEUE_INFORM_TEXT ) Queue queue,
                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_TEXT).noargs();
    }
//
//
//
    //声明QUEUE_INFORM_IMAGE 队列
    @Bean(QUEUE_INFORM_IMAGE )
    public Queue QUEUE_INFORM_IMAGE(){
        return new Queue(QUEUE_INFORM_IMAGE );
    }
//
//
    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_QUEUE_INFORM_IMAGE (@Qualifier(QUEUE_INFORM_IMAGE  ) Queue queue,
                                             @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_IMAGE ).noargs();
    }
//
//
//
    //声明QUEUE_INFORM_AUDIO 队列
    @Bean(QUEUE_INFORM_AUDIO)
    public Queue QUEUE_INFORM_AUDIO(){
        return new Queue(QUEUE_INFORM_AUDIO );
    }


    //ROUTINGKEY_TEXT队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_QUEUE_INFORM_AUDIO(@Qualifier(QUEUE_INFORM_AUDIO) Queue queue,
                                             @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_AUDIO).noargs();
    }

 
}