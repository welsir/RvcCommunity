package com.tml.pojo.dto;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @NAME: CommentDto
 * @USER: yuech
 * @Description:传递的数据模型
 * @DATE: 2023/11/29
 */
@Data
public class CommentDto {
//    内容
    @Length(min = 1, max = 1000, message = "长度为1到1000")
    @NotNull
    private String content;
//    所属帖子id
    @NotNull
    private String postId;
//    顶级评论id
    @NotNull
    private String rootCommentId;
//    回复目标用户id
    @NotNull
    private String toUserId;
    //回复评论id
    @NotNull
    private String toCommentId;
}