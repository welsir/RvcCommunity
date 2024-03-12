package com.tml.core.gateway.protocol.response;

import com.tml.common.AccessExceptionResponse;
import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/11 22:24
 */
@Component
@Data
public class CreateGroupResponse extends Packet {
    private boolean success;
    private String roomId;
    private AccessExceptionResponse exception;
    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_RESPONSE;
    }
}
