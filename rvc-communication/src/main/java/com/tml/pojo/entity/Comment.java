package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @NAME: Comment
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
@TableName("rvc_communication_comment")
public class Comment {
    //评论id
    @TableId
    private String postCommentId;
    //内容
    private String content;
    //评论时间
    private LocalDateTime createAt;
    //审核状态（0：审核中；1：审核通过；2、审核失败（不通过）；3、人工审核）
    private Integer detectionStatus;
    //所属用户id
    private String userId;
    //所属帖子id
    private String postId;
    //点赞次数
    private Long commentLikeCount;
    //顶级评论id
    private String rootCommentId;
    //回复目标用户id
    private String toUserId;
    //修改时间
    private LocalDateTime updateAt;
//    标签
    private String labels;
}