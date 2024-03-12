package com.tml.core.gateway.channel.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:06
 */
public interface ChannelHandler {

    void receive(Channel channel, Object message);

    void send(Channel channel, Object message);

    void connect(Channel channel);
}
