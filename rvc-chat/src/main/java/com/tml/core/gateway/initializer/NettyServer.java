package com.tml.core.gateway.initializer;

import com.tml.config.NettyServerConfiguration;
import com.tml.core.gateway.netty.NettyFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description websocket连接初始化Channel
 * @Author welsir
 * @Date 2024/2/19 15:36
 */
@Slf4j
@Component
public class NettyServer{
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    @Resource
    NettyServerConfiguration netty;
    public void start(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = NettyFactory.eventLoopGroup(1, "bossLoopGroup");
        workerGroup = NettyFactory.eventLoopGroup(4, "workerLoopGroup");
        bootstrap.group(bossGroup, workerGroup)
                //指定Channel类型（linux、win）
                .channel(NettyFactory.serverSocketChannelClass())
                //设置服务端的接受队列大小
                .option(ChannelOption.SO_BACKLOG, 2048)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(64 * 1024, 128 * 1024))
                .childHandler(new NettyServerInitializer(netty.getWsPath(),netty.getMaxContentLength()));
        bootstrap.bind(netty.getPort()).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("websocket端口绑定成功 [port]-> " + netty.getPort());
            } else {
                System.out.println("websocket端口绑定失败");
            }
        });
    }
}
