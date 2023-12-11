package com.tml.config;

import feign.Logger;
import feign.form.spring.SpringFormEncoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description
 * @Author welsir
 * @Date 2023/12/11 20:45
 */
@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL; // 设置全局日志级别为FULL
    }

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    //微服务传输文件用
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

}
