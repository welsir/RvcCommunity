package com.tml.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS("200","操作成功"),
    // 登录
    NEED_LOGIN("401","需要登录后操作"),

    SYSTEM_ERROR("500","出现错误");

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
