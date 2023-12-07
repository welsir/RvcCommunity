package com.tml.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.constant.dbTableConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = dbTableConfig.RVC_FEEDBACK_TYPE )
public class FeedbackTypeDO {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String type;
}
