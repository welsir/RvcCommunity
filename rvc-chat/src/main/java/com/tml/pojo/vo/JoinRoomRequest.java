package com.tml.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 19:37
 */
@Data
public class JoinRoomRequest implements Serializable {

    private String roomId;
    private String password;


}
