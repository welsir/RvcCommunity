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
    private Integer hasShow;
    private String violationInformation;
}