package com.tml.config;

import com.tml.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Component
public class WebConfiguration implements WebMvcConfigurer {



    @Autowired
    UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/**");
//                .excludePathPatterns("/api/v0/admin/**")
//                .excludePathPatterns("/api/v0/user/user/**","/api/v0/base/**","/api/v0/product/category/**","/api/v0/product/product/**")
//                .excludePathPatterns("/doc.html","/webjars/**","/v2/api-docs/**","/swagger-resources/**","/favicon.ico")
//                .excludePathPatterns("/files/**")
//                .excludePathPatterns("/static/**");
    }

}
