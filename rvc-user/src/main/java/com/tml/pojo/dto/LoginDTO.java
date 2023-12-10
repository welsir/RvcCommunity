package com.tml.pojo.dto;

import lombok.Data;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Data
public class LoginDTO {
    String email;

    String emailCode;

    String password;
}
