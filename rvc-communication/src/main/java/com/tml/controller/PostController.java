package com.tml.controller;

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

    /**
     * 获取交流帖子列表
     */
    @GetMapping("/list")
    public Result list(){
        return Result.success(API_NOT_IMPLEMENTED);
    }

    /**
     * 获取某个帖子详情信息
     */
    @GetMapping("/details")
    public Result details(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 点赞帖子  [T]
     */
    @GetMapping("/favorite")
    public Result favorite(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 收藏帖子 [T]
     */
    @GetMapping("/collection")
    public Result collection(){
        return Result.success(API_NOT_IMPLEMENTED);
    }




    /**
     * 发布帖子  [T]  [审]
     */
    @GetMapping("/add")
    public Result add(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 删除帖子   [T]
     */
    @GetMapping("/delete")
    public Result delete(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 修改我发布的帖子  [T]  [审]
     */
    @GetMapping("/update")
    public Result update(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 上传帖子封面  [T]  [审]
     */
    @GetMapping("/cover")
    public Result cover(){
        return Result.success(API_NOT_IMPLEMENTED);
    }

}