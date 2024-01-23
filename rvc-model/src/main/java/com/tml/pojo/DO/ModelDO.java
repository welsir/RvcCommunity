package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.constant.TimeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 19:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rvc_model")
public class ModelDO {
    private Long id;
    private String name;
    private String typeId;
    private Long collectionNum;
    private Long likesNum;
    private Long viewNum;
    private String note;
    private String description;
    private String picture;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime updateTime;
    private String hasShow;
    private boolean hasDelete;
}
