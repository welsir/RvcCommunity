package com.tml.core.gateway.protocol.request;

import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.command.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/7 19:51
 */
@Component
@Data
public class CreateGroupRequest extends Packet {

    private String uid;

    private String roomId;

    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_REQUEST;
    }
}
