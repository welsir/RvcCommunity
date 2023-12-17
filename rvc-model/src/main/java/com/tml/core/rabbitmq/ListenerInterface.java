package com.tml.core.rabbitmq;

import com.tml.pojo.DTO.DetectionTaskDTO;
import org.springframework.amqp.core.Message;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/11 13:42
 */

public interface ListenerInterface {

    void sendMsgToMQ(DetectionTaskDTO taskDTO,String type);

    void receiveText(Message message);
}
