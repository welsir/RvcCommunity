package com.tml.core.gateway.initializer;

import com.tml.config.NettyServerConfiguration;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/25 15:22
 */
public class NettyServerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Resource
    NettyServerConfiguration netty;

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast("httpServerCodec",new HttpServerCodec())
        .addLast("httpObjectAggregator", new HttpObjectAggregator(2048))
        .addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
    }
}
