package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @NAME: CommentDo
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
@TableName("rvc_communication_comment")
public class CommentDo {
    //评论id
    @TableId
    private Long postCommentId;
    //内容
    private String content;
    //评论时间
    private LocalDateTime createAt;
    //展示状态（1：展示，0：不可展示，2，审核流程中）
    private Integer hasShow;
    //所属用户id
    private Long userId;
    //所属帖子id
    private Long postId;
    //点赞次数
    private Long commentLikeCount;
    //顶级评论id
    private Long rootCommentId;
    //回复目标评论id
    private Long toCommentId;
    //修改时间
    private LocalDateTime updateAt;
//    违规原因
    private String violationInformation;
}