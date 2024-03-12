package com.tml.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 16:56
 */
@Data
@Component
@Configuration
public class NettyServerConfiguration {

    @Value("${channel.handshake.wait}")
    private int handshakeWaitSecond;

    @Value("${netty.address}")
    private String nettyServer;

    @Value("${netty.protocol}")
    private String nettyProtocol;

    @Value("${netty.maxContentLength}")
    private int maxContentLength;

    @Value("${netty.websocket.port}")
    private int port;

    @Value("${netty.websocket.path}")
    private String wsPath;
}
