package com.tml.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @NAME: CoinDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/7
 */
@Data
public class CoinDto {
    @NotNull(message = "id 不能为空")
    private String id;

    //1、点赞
    //0、取消点赞
    @NotNull(message = "type 不能为空")
    private String type;
}