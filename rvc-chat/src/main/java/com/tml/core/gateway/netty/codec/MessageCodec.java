package com.tml.core.gateway.netty.codec;

import com.tml.common.AccessMessage;
import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.protocol.Packet;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/1 21:32
 */
@Component
public class MessageCodec {

    public static Map<Byte, Packet> packetMap = new HashMap<>();
    public static Map<Byte, Serializer> serializerMap = new HashMap<>();

    public void put(Byte key,Packet packet){
        packetMap.put(key,packet);
    }

    public void put(Byte key,Serializer serializer){
        serializerMap.put(key,serializer);
    }

    public Packet getPacketMap(Byte key){
        return packetMap.get(key);
    }

    public Serializer getSerializerMap(Byte key){
        return serializerMap.get(key);
    }

    public Packet decodeRequest(Byte cmd, Byte algorithm, byte[] body){
        Packet requestPacket = packetMap.get(cmd);
        Serializer serializer = serializerMap.get(algorithm);
        return requestPacket==null||serializer==null?null:Serializer.DEFAULT.deserialize(requestPacket,body);
    }
    public static void encode(ByteBuf buf,AccessMessage msg){
        byte[] body = Serializer.DEFAULT.serialize(msg.getBody());
        buf.writeBoolean(msg.isHeartbeat());
        buf.writeByte(msg.getVersion());
        if(msg.isHeartbeat()){
            return;
        }
        buf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        buf.writeByte(msg.getCmd());
        buf.writeByte(msg.getLength());
        buf.writeBytes(body);
    }

    public static AccessMessage decode(ByteBuf buf){
        AccessMessage message = new AccessMessage();
        try {
            message.setVersion(buf.readByte());
            message.setHeartbeat(buf.readBoolean());
            if(message.isHeartbeat()){
                return message;
            }
            message.setSerializerAlgorithm(buf.readByte());
            byte cmd = buf.readByte();
            message.setCmd(cmd);
            int length = buf.readByte();
            message.setLength(length);
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            Packet packet = Serializer.DEFAULT.deserialize(packetMap.get(cmd), bytes);
            System.out.println("解码[packet ="+packet+"]");
            System.out.println("解码结束");
            message.setBody(packet);
            return message;
        }catch (RuntimeException e){
            throw new RuntimeException();
        }

    }

}
