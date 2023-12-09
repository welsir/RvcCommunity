package com.tml.pojo.dto;

import lombok.Data;

/**
 * @NAME: CoinDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/7
 */
@Data
public class CoinDto {

    private String uid;

    private String id;
//        //1、点赞    添加关系表中的记录       post表 like_num +1
//        //0、取消点赞    删除关系表中的记录       post表 like_num -1
    private String type;
}