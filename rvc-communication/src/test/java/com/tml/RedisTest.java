package com.tml;

import com.tml.pojo.entity.PostType;
import com.tml.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_TYPE;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_POST_WATCH;

/**
 * @NAME: RedisTest
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/14
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisCache redisCache;

    @Test
    public void  addPostType(){
        ArrayList<PostType> postTypes = new ArrayList<>();
        postTypes.add(new PostType("1","前端","/img/tool-person.png"));
        postTypes.add(new PostType("2","后端","/img/tool-chat.png"));

        redisCache.setCacheList(RVC_COMMUNICATION_POST_TYPE,postTypes);
    }


    @Test
    public void  getWatch(){
        Object cacheObject = redisCache.getCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + 1);
        System.out.println(cacheObject);
    }

    @Test
    public void  addWatch(){
        LocalDateTime now = LocalDateTime.now();
        redisCache.setCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + 1,now);
//        redisCache.setCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + 1,"1");
        System.out.println("over");

    }

    @Test
    public void test(){
        Object cacheObject = redisCache.getCacheObject(RVC_COMMUNICATION_POST_WATCH + ":" + 1);
        System.out.println(cacheObject);

    }



}