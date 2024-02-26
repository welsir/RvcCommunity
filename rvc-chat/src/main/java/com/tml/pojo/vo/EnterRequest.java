package com.tml.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 16:43
 */
@Data
public class EnterRequest implements Serializable {

    private static final long serialVersionUID = -938932330432571525L;

    private String roomId;
    private String uid;
    private String password;
}
