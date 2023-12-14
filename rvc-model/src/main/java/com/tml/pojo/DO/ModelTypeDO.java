package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/12 20:07
 */
@Data
@TableName("rvc_model_type")
@Builder
public class ModelTypeDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String type;

}
