package com.tml.pojo.dto;

import lombok.Data;

@Data
public class DetectionStatusDto {


    private String id;

    //由调用者来判断是否违规
    private String labels;


    //废弃字段
    private String name;



}