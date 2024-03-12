package com.tml.core.gateway.protocol.command;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/7 19:57
 */
public interface Command {

    Byte CREATE_GROUP_REQUEST = 1;
    Byte JOIN_GROUP_REQUEST = 2;
//    Byte QUIT_GROUP_REQUEST = 3;
//    Byte SEND_GROUP_MESSAGE_REQUEST = 4;
//    Byte HEART_BEAT_REQUEST = 5;

    Byte CREATE_GROUP_RESPONSE = 101;

    Byte JOIN_GROUP_RESPONSE =102;
//
//    Byte QUIT_GROUP_RESPONSE = 103;
//
//    Byte SEND_GROUP_MESSAGE_RESPONSE = 104;
}
