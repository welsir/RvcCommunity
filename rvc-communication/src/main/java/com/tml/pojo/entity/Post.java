package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST;

/**
 * @NAME: Post
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@Builder
@TableName(RVC_COMMUNICATION_POST)
@AllArgsConstructor
@NoArgsConstructor
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
    private String coverId;
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
    //审核状态（0：审核中；1：审核通过；2、审核失败（不通过）；3、人工审核）
    private Integer detectionStatus;

    private String labels;
//    (1:帖子被删除  0：未删除)
    private Integer hasDelete;
}