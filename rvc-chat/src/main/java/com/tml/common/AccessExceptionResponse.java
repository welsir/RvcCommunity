package com.tml.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/10 19:57
 */
@Data
@AllArgsConstructor
public class AccessExceptionResponse {

    public static final AccessExceptionResponse SUCCESS = new AccessExceptionResponse(200,"成功");

    public static final AccessExceptionResponse ROOM_NOT_EXIT = new AccessExceptionResponse(8010,"房间不存在");
    private int code;
    private String data;

}
