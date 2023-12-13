package com.tml.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotNull(message = "邮箱不能为空")
    @Length(min = 6, max = 30, message = "邮箱长度必须在6到30之间")
    @Email( message = "参数必须为邮箱")
    String email;

    @Length(min = 4, max = 10, message = "验证码长度必须在4到6之间")
    String emailCode;

    @Length(min = 8, max = 16, message = "密码长度必须在8到16之间")
    String password;
}
