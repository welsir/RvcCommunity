package com.tml.util;

import com.tml.common.captcha.Result;
import com.tml.exception.ServerException;
import com.tml.pojo.enums.EmailEnums;
import com.tml.pojo.enums.ResultEnums;
import com.tml.common.captcha.CaptchaService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2023/12/11
 * @Author xiaochun
 */
@Component
public class EmailUtil {
    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    CaptchaService captchaService;


    public void sendCode(String email, EmailEnums enums){
        Result<Map<String, String>> result = captchaService.email(email);
        if(result.getCode() != 200){
            throw new ServerException(ResultEnums.FAIL_SEND_VER_CODE);
        }
        redisTemplate.opsForValue().set(enums.getCodeHeader() + email, result.getData().get("code"), 5, TimeUnit.MINUTES);
    }

    public boolean verify(String email, EmailEnums enums, String code){
        String c = redisTemplate.opsForValue().get(enums.getCodeHeader() + email);
        return c != null && c.equals(code);
    }
}