package com.tml;

import com.tml.config.NettyServerConfiguration;
import com.tml.core.gateway.initializer.NettyServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/24 17:48
 */
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {


    @Resource
    private NettyServerConfiguration netty;

    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent()==null){
            if ("websocket".equals(netty.getNettyProtocol().toLowerCase())) {
                nettyServer.start();
            }
        }
    }
}
