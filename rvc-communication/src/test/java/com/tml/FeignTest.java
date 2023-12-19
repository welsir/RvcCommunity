package com.tml;

import com.tml.feign.user.RvcUserServiceFeignClient;
import com.tml.pojo.VO.UserInfoVO;
import io.github.common.web.Result;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @NAME: FeignTest
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {
    @Autowired
    private RvcUserServiceFeignClient rvcUserServiceFeignClient;

    @Test
    public void  getUser(){
       Result<UserInfoVO> res = rvcUserServiceFeignClient.one("1735662165315596290");
        System.out.println(res);
        System.out.println(res.getData());
    }

    @Test
    public void getUserList(){
        List<String> userIds = new ArrayList<>();
        userIds.add("1734216713637244929");
        userIds.add("1735662165315596290");
        Result<Map<String, List<UserInfoVO>>> list = rvcUserServiceFeignClient.list(userIds);
        System.out.println(list);
        System.out.println(list.getData().get("userList"));
    }

}