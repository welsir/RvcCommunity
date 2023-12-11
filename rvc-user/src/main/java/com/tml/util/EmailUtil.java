package com.tml.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Date 2023/12/11
 * @Author xiaochun
 */
@Component
public class EmailUtil {
    @Resource
    RedisTemplate<String, String> redisTemplate;

    public Map<String, String> sendCode(String email, String msg){
        redisTemplate.opsForValue().set(msg + ":" + email, "1111");
        // 利用fegin发送邮件
        return null;
    }

    public boolean verify(String email, String msg, String code){
        String c = redisTemplate.opsForValue().get(msg + ":" + email);
        return c != null && c.equals(code);
    }
}