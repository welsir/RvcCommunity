package com.tml.util;

import com.tml.client.CaptchaServiceClient;
import com.tml.config.CodeCofig;
import com.tml.exception.ServerException;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.Result;
import com.tml.pojo.enums.EmailEnums;
import com.tml.pojo.enums.ResultEnums;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    StringRedisTemplate stringRedisTemplate;

    @Resource
    CaptchaServiceClient captchaService;

    @Resource
    UserInfoMapper userInfoMapper;


    public void sendCode(String email, EmailEnums enums){
        switch (enums){
            case LOGIN:
            case PASSWORD:
                if(!userInfoMapper.exist("email", email)){
                    throw new ServerException(ResultEnums.ACCOUNT_NOT_EXIST);
                }
                break;
            case REGISTER:
                if(userInfoMapper.exist("email", email)){
                    throw new ServerException(ResultEnums.EMAIL_EXIST);
                }
                break;
        }

        Result<Map<String, String>> result = captchaService.email(email);
        if(result.getCode() != 200){
            throw new ServerException(ResultEnums.FAIL_SEND_VER_CODE);
        }

        stringRedisTemplate.opsForValue().set(CodeCofig.EMAIL_BASE + enums.getCodeHeader() + email, result.getData().get("code"), 5, TimeUnit.MINUTES);
    }

    public boolean emailVerify(String email, EmailEnums enums, String code){
        String c = stringRedisTemplate.opsForValue().get(CodeCofig.EMAIL_BASE + enums.getCodeHeader() + email);
        if(c != null && c.equals(code)){
            stringRedisTemplate.delete(enums.getCodeHeader() + email);
            return true;
        }
        return false;
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
        stringRedisTemplate.opsForValue().set(CodeCofig.IMAGE_BASE + uuid, code, 2, TimeUnit.MINUTES);
        return Map.of("base64", base64, "uuid", uuid);
    }

    public boolean preVerify(String uuid, String code){
        String c = stringRedisTemplate.opsForValue().get(CodeCofig.IMAGE_BASE + uuid);
        if(c != null && c.equals(code)){
            stringRedisTemplate.delete(uuid);
            return true;
        }
        return false;
    }
}