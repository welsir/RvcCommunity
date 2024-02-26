package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_task
 */
@TableName(value ="rvc_level_task")
@Data
public class RvcLevelTask implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private String taskName;

    /**
     * 
     */
    private String taskUrl;

    /**
     * 
     */
    private String privilegeCode;

    /**
     * 
     */
    private String rule;

    private Integer exp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}