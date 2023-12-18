package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_COLLECT;

/**
 * @NAME: CollectPost
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(RVC_COMMUNICATION_POST_COLLECT)
public class CollectPost {
    @TableField
    private String postId;
    private String uid;
    private String collectId;
}