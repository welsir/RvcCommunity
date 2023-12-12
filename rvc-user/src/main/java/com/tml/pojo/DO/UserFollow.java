package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@TableName("rvc_user_follow")
@Data
public class UserFollow {
    private String followId;

    private String followUid;

    private String followedUid;
}
