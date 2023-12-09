package com.tml.pojo.dto;

import lombok.Data;

/**
 * @NAME: CommentDto
 * @USER: yuech
 * @Description:传递的数据模型
 * @DATE: 2023/11/29
 */
@Data
public class CommentDto {
//    内容
    private String content;
//    所属用户id
    private String userId;
//    所属帖子id
    private String postId;
//    顶级评论id
    private String rootCommentId;
//    回复目标用户id
    private String toUserId;
    //回复评论id
    private String toCommentId;
}