package com.tml.pojo.vo;

import com.tml.core.gateway.protocol.Packet;
import com.tml.core.gateway.protocol.command.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 19:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class JoinGroupRequest extends Packet {

    private String uid;
    private String roomId;
    private String password;

    @Override
    public Byte getCommand() {
        return Command.JOIN_GROUP_REQUEST;
    }
}
