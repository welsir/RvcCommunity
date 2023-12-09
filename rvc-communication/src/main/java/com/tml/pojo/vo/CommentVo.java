package com.tml.pojo.vo;

import com.tml.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @NAME: CommentVo
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    //评论id
    private String postCommentId;
    //内容
    private String content;
    //评论时间
    private LocalDateTime createAt;
    //所属用户id
    private String userId;
    //所属帖子id
    private String postId;
    //点赞次数
    private Long commentLikeCount;
    //顶级评论id
    private String rootCommentId;
    //回复目标用户id
    private String toUserId;
    //修改时间
    private LocalDateTime updateAt;


    private User user;

    private User replayUser;

    private List<CommentVo> childrenComment;
}