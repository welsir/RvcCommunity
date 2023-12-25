package com.tml.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.constant.dbTableConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TeamDO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = dbTableConfig.RVC_WEB_TEAM )
public class TeamDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 成员描述
     */
    private String description;

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 用户职责
     */
    private String role;
}
