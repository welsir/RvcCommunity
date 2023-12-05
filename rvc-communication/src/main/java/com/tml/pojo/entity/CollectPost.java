package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @NAME: CollectPost
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@TableName("rvc_communication_post_collect")
public class CollectPost {
    private String postId;
    private String uid;
    private String collectId;
}