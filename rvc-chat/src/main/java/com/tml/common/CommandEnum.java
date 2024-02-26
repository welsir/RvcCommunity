package com.tml.common;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:31
 */
public enum CommandEnum {

    CHATROOM_CREATE(1,"创建房间"),
    CHATROOM_JOIN(2,"加入房间"),

    CHATROOM_LEAVE(3,"离开房间"),

    CHATROOM_SEND(4,"发送消息");

    private int command;
    private String desc;

    CommandEnum(int command, String desc) {
        this.command = command;
        this.desc = desc;
    }

    public int getCommand() {
        return command;
    }
}
