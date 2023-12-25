package com.tml.domain.DTO;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 12:17
 */
@Data
public class UserInfoDTO {

    private String uid;
    private String username;
    private String birthday;
    private String nickname;
    private String followNum;
    private String fansNum;
    private String sex;
    private String avatar;
    private String description;
}
