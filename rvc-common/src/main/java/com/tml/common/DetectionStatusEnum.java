package com.tml.common;

import lombok.Data;

/**
 * 审核状态枚举类
 */
public enum DetectionStatusEnum {
    /**
     * 未检测
     */
    UN_DETECTION(0, "审核中"),
    /**
     * 检测通过
     */
    DETECTION_SUCCESS(1, "审核通过"),
    /**
     * 检测失败
     */
    DETECTION_FAIL(2, "审核失败"),
    /**
     * 人工审核
     */
    DETECTION_Manual(3, "人工审核");

    private Integer status;  //审核状态码
    private String msg;
    DetectionStatusEnum(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
