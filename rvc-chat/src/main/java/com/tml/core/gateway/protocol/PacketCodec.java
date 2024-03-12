package com.tml.core.gateway.protocol;

import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.protocol.command.Command;
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
 * @Date 2024/3/7 20:14
 */
@Component
public class PacketCodec  {

    static Map<Byte,Packet> packetMap = new HashMap<>();
    static Map<Byte, Serializer> serializerMap = new HashMap<>();

    public static void put(Byte key,Packet packet){
        packetMap.put(key,packet);
    }

    public static void put(Byte key,Serializer serializer){
        serializerMap.put(key,serializer);
    }

    public static Packet getPacketMap(Byte key){
        return packetMap.get(key);
    }

    public static Serializer getSerializerMap(Byte key){
        return serializerMap.get(key);
    }

    public Packet decodeRequest(Byte cmd, Byte algorithm, Packet requestType){
        byte[] body = Serializer.DEFAULT.serialize(requestType);
        Packet requestPacket = packetMap.get(cmd);
        Serializer serializer = serializerMap.get(algorithm);
        return requestPacket==null||serializer==null?null:Serializer.DEFAULT.deserialize(requestPacket,body);
    }

}
