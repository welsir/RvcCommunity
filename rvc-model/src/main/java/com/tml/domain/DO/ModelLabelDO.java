package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 18:37
 */
@Data
@TableName("rvc_model_model_label")
public class ModelLabelDO {

    private String modelId;
    private String labelId;

}
