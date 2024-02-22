package com.tml.core.store.redis;

import com.tml.pojo.dto.ChatRoom;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 14:28
 */
@Component
public class RedisStoreImpl {

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    public void save(ChatRoom room) {


    }
}
