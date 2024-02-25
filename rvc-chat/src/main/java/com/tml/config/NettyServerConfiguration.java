package com.tml.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 16:56
 */
@Data
@Component
public class NettyServerConfiguration {

    @Value("${channel.handshake.wait}")
    private int handshakeWaitSecond;

    @Value("${netty.server}")
    private String nettyServer;

    @Value("${netty.protocol}:websocket")
    private String nettyProtocol;

    @Value("${netty.maxContentLength}")
    private int maxContentLength;

    @Value("${netty.ws_port}")
    private int port;
}
