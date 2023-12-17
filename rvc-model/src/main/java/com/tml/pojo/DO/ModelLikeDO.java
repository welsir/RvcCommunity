package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/12 0:02
 */
@TableName("rvc_model_likes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelLikeDO{
    private String modelId;
    private String uid;
}
