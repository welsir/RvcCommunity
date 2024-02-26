package com.tml.core.gateway.channel.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

import java.nio.channels.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:06
 */
public interface Handler {

    Object handle(ChannelHandlerContext channel, JSONObject message);

    default void receive(ChannelHandlerContext channel, JSONObject message) {
        handle(channel, message);
    }

}
