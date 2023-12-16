package com.tml.pojo.vo;

import com.tml.pojo.entity.PostType;
import com.tml.pojo.entity.UserInfoVO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @NAME: PostSimpleVo
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/16
 */
@Data
public class PostSimpleVo {
    //          帖子id
    private String postId;

    private PostType postType;

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

}