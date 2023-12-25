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
@TableName(value = dbTableConfig.RVC_WEB_TOOL    )
public class WebToolDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 工具栏图片
     */
    private String img;
    /**
     * 工具栏名称
     */
    private String tool;
    /**
     * 工具栏对应url
     */
    private String url;
}
