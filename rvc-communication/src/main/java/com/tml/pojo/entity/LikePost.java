package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @NAME: LikePost
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@TableName("rvc_communication_post_likes")
public class LikePost {
    private String postId;
    private String uid;
    private String likeId;
}