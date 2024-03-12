package com.tml.core.gateway.channel.handler;

import io.netty.channel.Channel;
import org.springframework.util.Assert;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:07
 */
public abstract class AbstractChannelHandler implements ChannelHandler{

    protected final ChannelHandler handler;

    protected AbstractChannelHandler(ChannelHandler handler) {
        Assert.notNull(handler, "handler == null");
        this.handler = handler;
    }

    @Override
    public void receive(Channel channel, Object message) {
        handler.receive(channel,message);
    }

    @Override
    public void send(Channel channel, Object message) {
        handler.send(channel,message);
    }

    @Override
    public void connect(Channel channel) {
        handler.connect(channel);
    }
}
