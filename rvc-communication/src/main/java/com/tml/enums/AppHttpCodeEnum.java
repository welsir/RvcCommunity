package com.tml.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS("200","操作成功"),
    // 登录
    NEED_LOGIN("401","需要登录后操作"),

    QUERY_ERROR("303","参数校验出错"),

    SYSTEM_ERROR("500","出现错误"),
    COMMENT_ERROR("501","评论不存在"),
    FAVORITE_ERROR("505","不允许重复点赞"),
    TYPE_ERROR("506","类型错误"),
    POST_ERROR("507","帖子不存在"),
    ;

    private String code;
    private String msg;

    AppHttpCodeEnum(String code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
