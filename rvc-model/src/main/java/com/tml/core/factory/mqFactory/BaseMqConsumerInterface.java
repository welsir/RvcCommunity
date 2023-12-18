package com.tml.core.factory.mqFactory;

import org.springframework.amqp.core.Message;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/15 8:21
 */
public interface BaseMqConsumerInterface {
    Object receiveText(Message message);

    Object receiveImage(Message message);

    Object receiveAudio(Message message);

}
