package com.tml;

import com.tml.config.NettyServerConfiguration;
import com.tml.core.gateway.initializer.NettyServer;
import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.netty.serialize.SerializerAlgorithm;
import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.PacketCodec;
import com.tml.core.gateway.protocol.command.Command;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/24 17:48
 */
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private NettyServerConfiguration netty;
    @Resource
    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent()==null){
            System.out.println(netty.getNettyProtocol().toLowerCase());
            if ("websocket".equals(netty.getNettyProtocol().toLowerCase())) {
                nettyServer.start();
            }
        }
    }
}
