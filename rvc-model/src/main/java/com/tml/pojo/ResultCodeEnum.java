package com.tml.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:43
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    /**
     * 成功
     */
    SUCCESS(200, "成功"),
    ADD_MODEL_SUCCESS(201,"添加模型成功"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常"),
    ADD_MODEL_FAIL(511,"模型添加失败"),
    QUERY_MODEL_FAIL(512,"查询模型失败"),
    QUERY_MODEL_LIST_FAIL(513,"获取模型列表失败"),
    UPLOAD_MODEL_FAIL(514,"上传模型失败"),
    UPLOAD_IMAGE_FAIL(515,"图片上传失败");


    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String msg;
}
