package com.tml.core.gateway.initializer;

import com.tml.config.NettyServerConfiguration;
import com.tml.core.gateway.handler.CenterServerHandler;
import com.tml.core.gateway.handler.NettyServerHandler;
import com.tml.core.gateway.netty.codec.WebSocketPacketCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/25 15:22
 */
@Component
public class NettyServerInitializer extends ChannelInitializer<NioSocketChannel> {

    private static final NettyServerHandler NETTY_SERVER_HANDLER = new NettyServerHandler(new CenterServerHandler());

    @Resource
    NettyServerConfiguration netty;

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast("httpServerCodec",new HttpServerCodec())
        .addLast("httpObjectAggregator", new HttpObjectAggregator(netty.getMaxContentLength()))
        .addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler(netty.getWsPath()))
        .addLast("chunkedWriteHandler",new ChunkedWriteHandler())
        .addLast("codec",WebSocketPacketCodec.INSTANCE)
        .addLast("handler",NETTY_SERVER_HANDLER);
    }
}
