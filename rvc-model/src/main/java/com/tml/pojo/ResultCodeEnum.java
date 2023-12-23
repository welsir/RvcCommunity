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
    UPLOAD_IMAGE_FAIL(515,"图片上传失败"),
    GET_USER_INFO_FAIL(516,"获取用户信息异常"),
    UPDATE_MODEL_VIEWS_FAIL(517,"更新模型浏览量失败"),
    ADD_MODEL_LABEL_FAIL(518,"添加模型标签失败"),
    SORT_FAIL(519,"排序参数错误"),
    PARAM_ID_IS_ERROR(520,"参数ID不正确"),
    INSERT_MODEL_USER_RELATIVE_FAIL(521,"插入模型-用户关系表失败"),
    ADD_COMMENT_FAIL(522,"评论失败"),
    USER_LIKES_ERROR(523,"无法重复点赞"),
    MODEL_FILE_ILLEGAL(524,"模型文件不合法"),
    LABEL_NOT_EXIT(525,"模型标签不存在"),
    TYPE_NOT_EXIT(526,"模型类型不存在"),
    PARAMS_ERROR(527,"参数错误"),
    GET_TYPE_ERROR(528,"获取不到模型类型"),
    GEY_LABEL_ERROR(529,"获取不到模型标签"),
    USER_COLLECTION_ERROR(530,"无法重复收藏"),
    COMMENT_NOT_EXITS(531,"评论不存在"),
    MODEL_NOT_EXITS(532,"模型不存在"),
    FILE_IS_NULL(533,"文件为空"),
    LABEL_IS_EXIT(534,"标签已存在"),
    UPLOAD_AUDIO_FAIL(535,"音频文件不合法");


    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String msg;
}
