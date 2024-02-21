package com.tml.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName rvc_level_privilege
 */
@TableName(value ="rvc_level_privilege")
@Data
public class RvcLevelPrivilege implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 权限名
     */
    private String privilegeName;

    /**
     * 权限描述信息
     */
    private String privilegeDescription;

    /**
     * 特权码
     */
    private String privilegeCode;

    /**
     * 资源路径
     */
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}