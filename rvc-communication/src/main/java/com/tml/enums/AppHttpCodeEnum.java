package com.tml.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS("200","操作成功"),
    // 登录
    NEED_LOGIN("401","需要登录后操作"),
    PERMISSIONS_ERROR("402","需要登录后操作"),



    QUERY_ERROR("303","参数校验出错"),
    NOT_FAVORITE_ERROR("304","用户没有喜欢"),
    NOT_COLLECT_ERROR("305","用户没有收藏"),
    NOT_CREATE_ERROR("305","用户没有创建"),

    SERVICE_ERROR("309","调用服务出现错误"),

    SYSTEM_ERROR("500","出现错误"),
    COMMENT_ERROR("501","评论不存在"),
    COLLECT_ERROR("504","不允许重复收藏"),
    FAVORITE_ERROR("505","不允许重复点赞"),
    TYPE_ERROR("506","类型错误"),
    POST_ERROR("507","帖子不存在或违规"),
    TAG_ERROR("508","标签不存在"),
    COVER_ERROR("508","封面不存在"),
    DETECTION_ERROR("509","审核失败"),
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
