package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 1
 * @Author welsir
 * @Date 2023/12/12 0:03
 */
@TableName("rvc_model_collection")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelCollectionDO{
    private String modelId;
    private String uid;
}
