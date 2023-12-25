package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/13 22:30
 */
@Data
@TableName("rvc_model_label")
public class LabelDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String label;
    private String createTime;
    private String hasShow;
}
