package com.tml.core.gateway.channel.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

import java.nio.channels.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:07
 */
public abstract class AbstractHandler implements Handler{
    @Override
    public Object handle(ChannelHandlerContext channel, JSONObject message) {
        return doHandle(channel,message);
    }
    protected abstract Object doHandle(ChannelHandlerContext channel, JSONObject message);
}
