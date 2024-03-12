package com.tml.core.gateway.protocol;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/7 19:51
 */
public abstract class Packet {

    public abstract Byte getCommand();

}
