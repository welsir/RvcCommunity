package com.tml.core.gateway.handler;

import com.tml.common.AccessMessage;
import io.netty.channel.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/6 20:44
 */
public abstract class AbstractHandler implements Handler{


    @Override
    public Object handle(Channel channel, AccessMessage message) {
        return doHandle(channel, message);
    }

    protected abstract Object doHandle(Channel channel, AccessMessage message);
}
