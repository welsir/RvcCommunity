package com.tml.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Date 2023/12/13
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private LocalDateTime birthday;

    private String nickname;

    private String sex;

    private String avatar;

    private String description;
}
