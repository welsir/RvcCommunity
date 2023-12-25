package com.tml.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.constant.dbTableConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = dbTableConfig.RVC_WEB_INFO )
public class WebInfoDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 官方联系方式
     */
    private String officialConcat;
    /**
     * rvc描述
     */
    private String rvcDescription;
    /**
     * rvc版本
     */
    private String rvcVersion;
    /**
     * 网站描述
     */
    private String webDescription;
    /**
     * 网站名称
     */
    private String webName;
}
