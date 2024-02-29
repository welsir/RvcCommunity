package com.tml.controller;

import com.google.gson.Gson;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.pojo.vo.UserInfoVO;
import com.tml.service.TopService;
import io.github.common.web.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * @Date 2024/2/28
 * @Author xiaochun
 */
@RestController
@Validated
@RequestMapping("/top")
public class TopController {
    @Resource
    TopService topService;

    @GetMapping("/userTop")
    @LaxTokenApi
    public Result userTop(){
        return Result.success(topService.userTop());
    }

//    @GetMapping("/moudelTop")
//    @LaxTokenApi
//    public Result moudelTop(){
//        return Result.success(topService.);
//    }
}
