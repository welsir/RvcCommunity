package com.tml.util;

import com.tml.pojo.DO.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Component
public class UserUtil {

    @Autowired
    private HttpServletRequest request;

    public AuthUser getCurrentUser(){
        return new AuthUser(request.getHeader("uid"), request.getHeader("username"));
    }
}
