package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.constant.TimeConstant;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Long hot;
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createTime;
    private String hasShow;
}
