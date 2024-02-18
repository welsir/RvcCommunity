package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;

/**
 * @NAME: PostType
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/28
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(RVC_COMMUNICATION_POST_TYPE )
public class PostType {

    @TableId(value = "tag_id", type = IdType.AUTO)
    private String id;

    private String tagImg;

    private String tagName;

}
