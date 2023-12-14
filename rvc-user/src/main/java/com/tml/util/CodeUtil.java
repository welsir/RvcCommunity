package com.tml.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.tml.common.captcha.Result;
import com.tml.exception.ServerException;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.enums.EmailEnums;
import com.tml.pojo.enums.ResultEnums;
import com.tml.common.captcha.CaptchaService;
import org.apache.catalina.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2023/12/11
 * @Author xiaochun
 */
@Component
public class CodeUtil {
    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    CaptchaService captchaService;

    @Resource
    UserInfoMapper userInfoMapper;


    public void sendCode(String email, EmailEnums enums){
        QueryWrapper<UserInfo> userWrapper = new QueryWrapper<>();
        userWrapper.eq("email", email);
        switch (enums){
            case LOGIN:
            case PASSWORD:
                if(userInfoMapper.selectCount(userWrapper) <= 0){
                    throw new ServerException(ResultEnums.ACCOUNT_NOT_EXIST);
                }
                break;
            case REGISTER:
                if(userInfoMapper.selectCount(userWrapper) > 0){
                    throw new ServerException(ResultEnums.EMAIL_EXIST);
                }
                break;
        }

        Result<Map<String, String>> result = captchaService.email(email);
        if(result.getCode() != 200){
            throw new ServerException(ResultEnums.FAIL_SEND_VER_CODE);
        }

        redisTemplate.opsForValue().set(enums.getCodeHeader() + email, result.getData().get("code"), 5, TimeUnit.MINUTES);
    }

    public boolean emailVerify(String email, EmailEnums enums, String code){
        String c = redisTemplate.opsForValue().get(enums.getCodeHeader() + email);
        return c != null && c.equals(code);
    }

    public Map<String, String> image(){
        Result<Map<String, String>> result = captchaService.image();
        if(result.getCode() != 200){
            throw new ServerException(ResultEnums.FAIL_GET_IMAGE_CODE);
        }
        Map<String, String> map = result.getData();
        String base64 = map.get("base64");
        String code = map.get("code");
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuid, code, 2, TimeUnit.MINUTES);
        return Map.of("base64", base64, "uuid", uuid);
    }

    public boolean preVerify(String uuid, String code){
        String c = redisTemplate.opsForValue().get(uuid);
        return c != null && c.equals(code);
    }
}