package com.tml.pojo.enums;

import lombok.Getter;

/**
 * @Date 2023/12/14
 * @Author xiaochun
 */
@Getter
public enum EmailEnums {
    /**
     * 注册验证码
     */
    REGISTER(0, "注册验证码", "Register:"),
    /**
     * 登录验证码
     */
    LOGIN(1, "登录验证码", "Login:"),
    /**
     * 修改密码验证码
     */
    PASSWORD(2, "修改密码验证码", "PASSWORD:"),
    FORGOT_PASSWORD(2, "忘记密码验证码", "FORGOT_PASSWORD:");
    private final int code;  //验证码编号
    private final String msg;
    private final String codeHeader; //redis前置信息
    EmailEnums(int code, String msg, String codeHeader){
        this.code = code;
        this.msg = msg;
        this.codeHeader = codeHeader;
    }
}
