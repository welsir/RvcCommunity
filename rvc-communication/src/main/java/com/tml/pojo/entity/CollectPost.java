package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @NAME: CollectPost
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rvc_communication_post_collect")
public class CollectPost {
    private String postId;
    private String uid;
    private String collectId;
}