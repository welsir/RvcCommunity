package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_LIKES;

/**
 * @NAME: LikePost
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(RVC_COMMUNICATION_POST_LIKES)
public class LikePost {
    @TableId
    private String likeId;
    private String postId;
    private String uid;

}