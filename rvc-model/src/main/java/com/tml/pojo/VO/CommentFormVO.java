package com.tml.pojo.VO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/17 14:48
 */
@Data
public class CommentFormVO {

    private String replyId;
    @NotBlank(message = "id不能为空")
    private String modelId;
    @NotBlank(message = "消息不能为空")
    private String content;
}
