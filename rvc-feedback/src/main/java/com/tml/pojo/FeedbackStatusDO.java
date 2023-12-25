package com.tml.pojo;

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
@TableName(value = dbTableConfig.RVC_FEEDBACK_STATUS )
public class FeedbackStatusDO {

    @TableId(value = "id")
    private Integer id;

    private String status;
}
