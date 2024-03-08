package com.tml.util;

import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.vo.UserInfoVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Date 2024/2/28
 * @Author xiaochun
 */
@Component
public class TopUtil {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public <E> void top(String key, List<E> data, Class<E> clazz){
//        stringRedisTemplate.opsForZSet().range(key, 0, 99)
//        List<Object> list = redisTemplate.opsForValue().multiGet();
//        CopyUtil.copyPropertiesForList(userList, data, clazz);
    }
}
