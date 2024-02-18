package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.constant.TimeConstant;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_COMMENT;

/**
 * @NAME: Comment
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
@TableName(RVC_COMMUNICATION_COMMENT)
public class Comment{
    //评论id
    @TableId
    private String postCommentId;
    //内容
    private String content;
    //审核状态（0：审核中；1：审核通过；2、审核失败（不通过）；3、人工审核）
    private Integer detectionStatus;
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

    @TableField(fill = FieldFill.INSERT)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime updateAt;
//    //修改时间
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private Date updateAt;
//    //评论时间
//    @TableField(fill = FieldFill.INSERT)
//    private Date createAt;
//    标签
    private String labels;
}