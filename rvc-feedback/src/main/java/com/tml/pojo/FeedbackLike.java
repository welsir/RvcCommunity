package com.tml.pojo;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tml.constant.dbTableConfig;
import io.github.constant.TimeConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = dbTableConfig.RVC_FEEDBACK_LIKE )
public class FeedbackLike {

    @TableId(value = "id")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long id;
    /**
     * 反馈ID
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long fbId;

    /**
     * 用户uid
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String uid;

    /**
     * 最近创建时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createAt;
}
