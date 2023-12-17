package com.tml.interceptor;


import com.tml.pojo.dto.LoginInfoDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Copyright (C),2021-2023
 * All rights reserved.
 * FileName: UserLoginInterceptor
 *
 * @author NEKOnyako
 * Description:
 * Date: 2023/10/09 0009 17:19
 */

@Component
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginInfoDTO> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("进入用户拦截器 {}", request.getRequestURI());
        // 从请求头中获取uid
        String uid = request.getHeader("uid");

        LoginInfoDTO loginInfoDTO = LoginInfoDTO.builder()
                .id(uid)
                .build();

        loginUser.set(loginInfoDTO);
            //通过，放行
            return true;
    }
}
