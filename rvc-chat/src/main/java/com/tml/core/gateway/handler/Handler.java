package com.tml.core.gateway.handler;

import com.tml.common.AccessMessage;
import io.netty.channel.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/6 20:47
 */
public interface Handler {

    Object handle(Channel channel, AccessMessage message);

    default void receive(Channel channel, AccessMessage message){
        handle(channel, message);
    }

}
