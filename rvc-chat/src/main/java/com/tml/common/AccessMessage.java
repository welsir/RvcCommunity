package com.tml.common;

import com.tml.core.gateway.protocol.Packet;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/11 22:28
 */
@Data
public class AccessMessage {

    private boolean heartbeat;
    private Byte version;
    private Byte cmd;
    private Byte serializerAlgorithm;
    private int length;
    private Packet body;
}
