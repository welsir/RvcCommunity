package com.tml.core.gateway.netty.serialize;


import com.tml.core.gateway.netty.serialize.Impl.JSONSerializer;
import com.tml.core.gateway.protocol.Packet;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/1 21:49
 */
public interface Serializer {

    /**
     * 默认序列化器
     */
    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法
     *
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     *
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     *
     * @param packet
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Packet packet, byte[] bytes);

}
