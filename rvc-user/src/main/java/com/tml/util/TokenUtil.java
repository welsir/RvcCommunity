package com.tml.util;

import cn.dev33.satoken.stp.StpUtil;

/**
 * @Date 2023/12/11
 * @Author xiaochun
 */
public class TokenUtil {
    public static String getToken(String id, String username){
        String loginId = id + "|" + username;
        StpUtil.login(loginId);
        return StpUtil.getTokenValueByLoginId(loginId);
    }

    public static void logout(String id, String username){
        StpUtil.logout(id + "|" + username);
    }
}
