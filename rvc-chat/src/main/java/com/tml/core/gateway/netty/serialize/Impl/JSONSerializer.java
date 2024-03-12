package com.tml.core.gateway.netty.serialize.Impl;

import com.alibaba.fastjson.JSON;
import com.tml.core.gateway.netty.serialize.Serializer;
import com.tml.core.gateway.netty.serialize.SerializerAlgorithm;
import com.tml.core.gateway.protocol.Packet;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/1 21:50
 */
@Component
public class JSONSerializer implements Serializer {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Packet packet, byte[] bytes) {
        return JSON.parseObject(bytes,packet.getClass());
    }
}
