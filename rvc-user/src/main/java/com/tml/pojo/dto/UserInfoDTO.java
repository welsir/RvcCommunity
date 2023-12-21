package com.tml.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.github.constant.TimeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.dev33.satoken.dao.SaTokenDaoRedisJackson.DATE_PATTERN;

/**
 * @Date 2023/12/13
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = DATE_PATTERN, timezone = "GMT+8")
    @NotNull
    private LocalDate birthday;

    @NotBlank
    private String nickname;

    @NotBlank
    private String sex;

    @NotBlank
    private String description;
}
