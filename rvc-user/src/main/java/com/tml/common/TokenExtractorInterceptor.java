package com.tml.common;

import cn.dev33.satoken.stp.StpUtil;
import com.tml.exception.ServerException;
import com.tml.pojo.AuthUser;
import com.tml.pojo.enums.ResultEnums;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2023/12/11
 * @Author xiaochun
 */
@Aspect
@Component
public class TokenExtractorInterceptor {
    private static final ThreadLocal<AuthUser> threadLocal = new ThreadLocal<>();

    @Before("@annotation(Login)")
    public void getToken(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest(joinPoint);
        if (request != null) {
            try {
                AuthUser authUser = new AuthUser();
                authUser.setUid(request.getHeader("uid"));
                authUser.setUsername(request.getHeader("username"));
                threadLocal.set(authUser);
            } catch (Exception e){
                throw new ServerException(ResultEnums.NO_LOGIN);
            }
        }
    }

    private HttpServletRequest getRequest(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest) arg;
            }
        }
        return null;
    }
}
