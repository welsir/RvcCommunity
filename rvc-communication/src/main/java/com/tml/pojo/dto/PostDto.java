package com.tml.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @NAME: PostDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/9
 */
@Data
public class PostDto {

    //        帖子类型
    @NotBlank(message = "tagId 参数不能为空")
    private String tagId;
    //        帖子标题
    @NotBlank(message = "title 参数不能为空")
    private String title;
    //        帖子内容
    @NotBlank(message = "content 参数不能为空")
    private String content;
    //        帖子封面
    @NotBlank(message = "coverId 参数不能为空")
    private String coverId;

}