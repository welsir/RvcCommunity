package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/14 12:38
 */
@Data
@TableName("rvc_model_model_user")
public class ModelUserDO {

    @TableId
    private String modelId;
    private String uid;

}
