package com.tml.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.constant.DBConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @NAME: PostTypeDo
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/28
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = DBConstant.RVC_COMMUNICATION_POST_TYPE )
public class PostTypeDo {

    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long id;

    private String tagName;

    private String tagImg;

}
