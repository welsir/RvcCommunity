package com.tml.pojo.VO;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Data
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
