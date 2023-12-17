package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @NAME: Cover
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/6
 */
@Data
@Builder
@TableName("rvc_communication_cover")
public class Cover {
    @TableId
    private String coverId;
    private String postId;
    private String coverUrl;
    private String createAt;
    //审核状态（0：审核中；1：审核通过；2、审核失败（不通过）；3、人工审核）
    private Integer detectionStatus;
    private String labels;
    private String uid;
}