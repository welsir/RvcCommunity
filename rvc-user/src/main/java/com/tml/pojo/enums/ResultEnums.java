package com.tml.pojo.enums;

import java.io.Serializable;

/**
 * @Date 2023/5/5
 * @Author xiaochun
 */
public enum ResultEnums implements Serializable {
    SUCCESS("200", "操作成功"),
    SERVER_ERROR("400", "服务器错误"),
    FAIL_LOGIN("450", "登录失败"),
    FAIL_REGISTER("451", "注册失败"),
    LOGIN_FORM_ERROR("453", "登录格式错误"),
    NO_LOGIN("454", "未登录"),
    FAIL_SEND_VER_CODE("521", "发送验证码失败"),
    EMAIL_EXIST("522", "邮箱已存在"),
    TOKEN_EXPIRED("523", "token已过期"),
    VER_CODE_ERROR("524", "验证码错误"),
    USERNAME_EXIST("525", "用户名已存在"),
    WRONG_USERNAME_OR_PASSWORD("526", "用户名或密码错误");

    private final String code;

    private final String message;

    ResultEnums(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
