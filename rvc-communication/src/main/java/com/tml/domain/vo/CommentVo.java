package com.tml.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tml.pojo.VO.UserInfoVO;
import io.github.constant.TimeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createAt;
//    //所属用户id
//    private String userId;
    //所属帖子id
    private String postId;
    //点赞次数
    private Long commentLikeCount;
    //顶级评论id
    private String rootCommentId;
//    //回复目标用户id
//    private String toUserId;
    //修改时间
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime updateAt;

    private UserInfoVO user;

    private UserInfoVO replayUser;

    private boolean like;

//    private List<CommentVo> childrenComment;
}

