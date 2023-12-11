package com.tml.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
