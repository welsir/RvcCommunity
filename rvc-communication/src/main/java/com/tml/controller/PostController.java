package com.tml.controller;

import com.tml.annotation.Detection;
import com.tml.annotation.SystemLog;
import io.github.common.web.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tml.constant.MessageConstant.API_NOT_IMPLEMENTED;

/**
 * @NAME: PostController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequestMapping("/communication/post")
public class PostController {


    @SystemLog(businessName = "获取交流帖子列表")
    @GetMapping("/list")
    public Result list(){
        return Result.success(API_NOT_IMPLEMENTED);
    }

    @GetMapping("/details")
    @SystemLog(businessName = "获取某个帖子详情信息")
    public Result details(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/favorite")
    @SystemLog(businessName = "点赞帖子  [T]")
    public Result favorite(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    @GetMapping("/collection")
    @SystemLog(businessName = "收藏帖子 [T]")
    public Result collection(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    public Result add(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/delete")
    @SystemLog(businessName = "删除帖子   [T]")
    public Result delete(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/update")
    @SystemLog(businessName = "修改我发布的帖子  [T]  [审]")
    public Result update(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/cover")
    @SystemLog(businessName = "上传帖子封面  [T]  [审]")
    public Result cover(){
        return Result.success(API_NOT_IMPLEMENTED);
    }

}