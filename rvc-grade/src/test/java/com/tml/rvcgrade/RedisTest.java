package com.tml.rvcgrade;

import com.tml.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * @NAME: RedisTest
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RedisTest {

    @Autowired
    public RedisTemplate redisTemplate;


    @Test
    public void test1(){

        // 获取ZSetOperations对象
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

// 添加元素到有序集合
        zSetOps.add("myZSet", "value1", 3.0);
        zSetOps.add("myZSet", "value2", 2.0);

//// 获取有序集合的成员数量
        Long size = zSetOps.size("myZSet");
        System.out.println("数量：" +size);

// 获取有序集合中指定成员的分数
        Double score = zSetOps.score("myZSet", "value1");
        System.out.println("score:" + score);

// 增加指定成员的分数
        Double newScore = zSetOps.incrementScore("myZSet", "value1", 5.0);
        System.out.println("newScore:" + newScore);

// 获取有序集合指定范围的元素
        Set<String> range = zSetOps.range("myZSet", 0, -1);


// 获取有序集合指定分数范围的元素
        Set<String> rangeByScore = zSetOps.rangeByScore("myZSet", 2.0, 4.0);

// 删除有序集合中的指定成员
        Long removedCount = zSetOps.remove("myZSet", "value2");


// 删除有序集合中指定分数范围的元素
        Long count = zSetOps.removeRangeByScore("myZSet", 0.0, 2.0);

    }



}