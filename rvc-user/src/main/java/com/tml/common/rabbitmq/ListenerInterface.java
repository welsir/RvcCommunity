package com.tml.common.rabbitmq;

import com.tml.pojo.dto.DetectionTaskDTO;
import org.springframework.amqp.core.Message;
/**
 * @Date 2023/12/17
 * @Author xiaochun
 */

public interface ListenerInterface {

    void sendMsgToMQ(DetectionTaskDTO task, String type);

    void receiveText(Message message);
}