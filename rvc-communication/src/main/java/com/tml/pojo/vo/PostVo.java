package com.tml.pojo.vo;

import com.tml.pojo.entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @NAME: PostVo
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVo {
    //          帖子id
    private String postId;
//    //        创建帖子用户id
//    private String uid;

    private UserInfoVO author;

    private PostType postType;

////作者用户名
//    private String username;
////    作者昵称
//    private String nickname;
////    作者头像
//    private String avatar;
    //        帖子类型
//    private String tagId;
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

    //是否点赞
    private boolean like;
    //是否收藏
    private boolean collect;

}