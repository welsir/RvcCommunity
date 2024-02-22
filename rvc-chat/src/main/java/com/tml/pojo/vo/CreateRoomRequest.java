package com.tml.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 16:00
 */
@Data
public class CreateRoomRequest implements Serializable {

    private static final long serialVersionUID = -2784248520516424354L;

    private String title;
    private String password;
}
