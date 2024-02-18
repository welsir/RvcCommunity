package com.tml.domain.dto;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nullable;
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

    @Length(min = 1, max = 1000, message = "content 长度为1到1000")
    @NotNull
    private String content;

    @NotNull(message = "postId 不能为空")
    private String postId;

//    @NotNulls(message = "rootCommentId 不能为空")
    private String rootCommentId;

//    @NotNull
//    @NotNull(message = "toUserId 不能为空")
    private String toUserId;

//    @NotNull
//    @NotNull(message = "toCommentId 不能为空")
    private String toCommentId;

    public void setRootCommentId(String rootCommentId) {
        if (Strings.isBlank(rootCommentId)) { // StringUtils 是 org.apache.commons.lang3 包下的工具类，用于判断字符串是否为空
            this.rootCommentId = "-1";
        } else {
            this.rootCommentId = rootCommentId;
        }
    }
}



