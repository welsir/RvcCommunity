package com.tml.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @NAME: CommentDto
 * @USER: yuech
 * @Description:传递的数据模型
 * @DATE: 2023/11/29
 */
@Data
public class CommentDto {

    @Length(min = 1, max = 1000, message = "content 长度为1到1000")
    @NotNull
    private String content;

    @NotNull(message = "postId 不能为空")
    private String postId;

//    @NotNull
//    @NotNulls(message = "rootCommentId 不能为空")
    private String rootCommentId;

//    @NotNull
//    @NotNull(message = "toUserId 不能为空")
    private String toUserId;

//    @NotNull
//    @NotNull(message = "toCommentId 不能为空")
    private String toCommentId;
}



