package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_COMMENT_LIKES;

/**
 * @NAME: LikeComment
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(RVC_COMMUNICATION_COMMENT_LIKES)
public class LikeComment {
    @TableId
    private String likeId;
    private String commentId;
    private String uid;

}