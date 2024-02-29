package com.tml.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Data
@Builder
public class UserInfoVO {
    private String uid;

    private String username;

    private LocalDate birthday;

    private String nickname;

    private int followNum;

    private int fansNum;

    private String sex;

    private String avatar;

    private String description;
}
