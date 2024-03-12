package com.tml.core.gateway.handler;

import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.netty.serialize.SerializerAlgorithm;
import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.PacketCodec;
import com.tml.core.gateway.protocol.command.Command;
import io.netty.util.internal.PlatformDependent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/11 21:08
 */
@Component
public class CenterRouter implements ApplicationListener<ContextRefreshedEvent> {
    private static final Map<Integer, String> HANDLES = new HashMap<>();
    private static final Map<String, Handler> HANDLER_WRAPPERS = PlatformDependent.newConcurrentHashMap();
    private static ApplicationContext applicationContext;

    @Resource
    private PacketCodec packetCodec;

    public static Handler router(int cmd){
        String name = HANDLES.get(cmd);
        Handler handler = applicationContext.getBean(name, Handler.class);
        return HANDLER_WRAPPERS.computeIfAbsent(name,k->handler);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        applicationContext = event.getApplicationContext();
        initPacketCodec();
    }

    private void initPacketCodec(){
        initPacket();
        initSerializer();
    }

    private void initPacket(){
        Map<String, Packet> beans = applicationContext.getBeansOfType(Packet.class);
        Field[] fields = Command.class.getDeclaredFields();
        for (Field field : fields) {
            String[] parts = field.getName().toLowerCase().split("_");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    sb.append(Character.toLowerCase(parts[i].charAt(0)))
                            .append(parts[i].substring(1));
                } else {
                    sb.append(Character.toUpperCase(parts[i].charAt(0)))
                            .append(parts[i].substring(1));
                }
            }
            try {
                byte val = (Byte) field.get(null);
                String beanName = sb.toString();
                if(isPacketBeanRegistered(beanName)){
                    Packet packet = beans.get(beanName);
                    packetCodec.put(val,packet);
                }else{
                    //初始化Packet解码对象异常
                    throw new RuntimeException("未找到响应包与指令对应关系, "+val+":"+beanName);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void initSerializer(){
        Map<String, Serializer> beans = applicationContext.getBeansOfType(Serializer.class);
        Field[] fields = SerializerAlgorithm.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                if(isPacketBeanRegistered(name)){
                    Byte val = field.getByte(null);
                    packetCodec.put(val,beans.get(name));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private boolean isPacketBeanRegistered(String name){
        return applicationContext.containsBean(name);
    }
}
