package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Date 2023/12/8
 * @Author xiaochun
 */
@TableName("rvc_user_follow")
@Data
public class UserFollow {
    @TableId(value = "follow_id")
    private String followId;

    private String followUid;

    private String followedUid;
}
