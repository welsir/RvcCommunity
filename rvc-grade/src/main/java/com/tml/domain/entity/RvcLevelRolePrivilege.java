package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_role_privilege
 */
@TableName(value ="rvc_level_role_privilege")
@Data
public class RvcLevelRolePrivilege implements Serializable {
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
    private String privilegeId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}