package com.tml.core.gateway.handler;

import com.tml.core.gateway.channel.handler.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/4 22:04
 */
@Slf4j
@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private final ChannelHandler handler;

    public NettyServerHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            initChannel(ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.receive(ctx.channel(),msg);
        super.channelRead(ctx, msg);
    }

    private void initChannel(Channel ch) {
        handler.connect(ch);
    }
}
