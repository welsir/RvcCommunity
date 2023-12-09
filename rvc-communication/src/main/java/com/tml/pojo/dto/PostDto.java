package com.tml.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @NAME: PostDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/9
 */
@Data
public class PostDto {

    private String uid;
    //        帖子类型
    private String tagId;
    //        帖子标题
    private String title;
    //        帖子内容
    private String content;
    //        帖子封面
    private String coverId;

}