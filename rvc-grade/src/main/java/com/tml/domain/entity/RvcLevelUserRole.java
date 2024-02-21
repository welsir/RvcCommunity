package com.tml.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_user_role
 */
@TableName(value ="rvc_level_user_role")
@Data
public class RvcLevelUserRole implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String uid;

    /**
     * 
     */
    private String roleId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}