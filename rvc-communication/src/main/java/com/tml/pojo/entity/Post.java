package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @NAME: Post
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@Builder
@TableName("rvc_communication_post")
public class Post {
//          帖子id
    @TableId
    private String postId;
//        创建帖子用户id
    private String uid;
//        帖子类型
    private String tagId;
//        帖子标题
    private String title;
//        帖子内容
    private String content;
//        帖子封面
    private String cover;
//        评论数
    private Long commentNum;
//        点赞数
    private Long likeNum;
//        收藏数
    private Long collectNum;
//        浏览数
    private Long watchNum;
//        创建日期
    private LocalDateTime createAt;
//        更新日期
    private LocalDateTime updateAt;
//        展示状态（1：展示，0：不可展示，2，审核流程中）
    private Integer hasShow;
}