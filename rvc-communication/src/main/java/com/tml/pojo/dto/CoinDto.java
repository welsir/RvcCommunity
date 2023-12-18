package com.tml.pojo.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @NAME: CoinDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/7
 */
@Data
public class CoinDto {
    @NotNull
    private String id;
//        //1、点赞    添加关系表中的记录       post表 like_num +1
//        //0、取消点赞    删除关系表中的记录       post表 like_num -1
    @NotNull
    private String type;
}