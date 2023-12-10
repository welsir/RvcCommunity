package com.tml.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */

@TableName("rvc_user_info")
@Data
public class UserInfo {
    private String uid;

    private String username;

    private String email;

    private String description;

    private String password;

    private String nickname;

    private String avatar;

    private String sex;

    private String phone;

    private LocalDateTime birthday;

    private LocalDateTime registerData;

    private LocalDateTime updatedAt;
}
