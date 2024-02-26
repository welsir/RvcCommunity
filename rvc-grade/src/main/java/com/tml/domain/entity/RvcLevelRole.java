package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_role
 */
@TableName(value ="rvc_level_role")
@Data
public class RvcLevelRole implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String roleName;

    /**
     * 
     */
    private Long minExp;

    /**
     * 
     */
    private Long maxExp;

    /**
     * 
     */
    private String roleDescription;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}