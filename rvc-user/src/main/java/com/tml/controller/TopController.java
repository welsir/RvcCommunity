package com.tml.controller;

import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.service.TopService;
import io.github.common.web.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

//    @GetMapping("/modelTop")
//    @LaxTokenApi
//    public Result modelTop(){
//        return Result.success(topService.modelTop());
//    }
//
//    @GetMapping("/postTop")
//    @LaxTokenApi
//    public Result postTop(){
//        return Result.success(topService.postTop());
//    }
}
