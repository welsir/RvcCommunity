package com.tml.pojo.dto;

import lombok.Data;

@Data
public class CommentStatusDto {


    private Long id;
    //展示状态(是否违规) 1：展示，0：不可展示
    private Integer status;

    private String violationInformation;

}
