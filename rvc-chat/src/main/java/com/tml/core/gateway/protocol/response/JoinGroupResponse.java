package com.tml.core.gateway.protocol.response;

import com.tml.common.AccessExceptionResponse;
import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.command.Command;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bouncycastle.util.Pack;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/9 19:16
 */
@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class JoinGroupResponse extends Packet {

    private String content;
    private boolean success;
    private AccessExceptionResponse exception;
    @Override
    public Byte getCommand() {
        return Command.JOIN_GROUP_RESPONSE;
    }
}
