package com.tml.common;

import com.tml.pojo.AuthUser;

/**
 * @Date 2023/12/12
 * @Author xiaochun
 */
public class UserContext {
    private static final ThreadLocal<AuthUser> threadLocal = new ThreadLocal<>();
    public static AuthUser getCurrentUser(){
        return threadLocal.get();
    }
}
