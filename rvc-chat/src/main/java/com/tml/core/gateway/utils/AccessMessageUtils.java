package com.tml.core.gateway.utils;

import com.tml.common.AccessMessage;
import com.tml.common.ProtocolConstant;
import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.PacketCodec;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/11 11:56
 */
public class AccessMessageUtils {

    public static AccessMessage createRequest(byte cmd, Object obj){

        AccessMessage message = new AccessMessage();
        message.setVersion(ProtocolConstant.VERSION);
        message.setCmd(cmd);
        return message;
    }

    public static AccessMessage createResponse(byte cmd,byte[] body){
        AccessMessage response = new AccessMessage();
        response.setCmd(cmd);
        Packet packet = PacketCodec.getPacketMap((byte) (cmd + 100));
        Assert.notNull(packet,"指令不正确");
        response.setBody(Serializer.DEFAULT.deserialize(packet,body));
        return response;
    }

}
