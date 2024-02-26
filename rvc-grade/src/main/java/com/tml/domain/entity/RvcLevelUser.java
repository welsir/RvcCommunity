package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_user
 */
@TableName(value ="rvc_level_user")
@Data
public class RvcLevelUser implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String uid;

//    /**
//     * 等级
//     */
//    private Integer level;

    /**
     * 经验值
     */
    private Long exp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}