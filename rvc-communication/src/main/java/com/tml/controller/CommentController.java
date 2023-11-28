package com.tml.controller;


import io.github.common.web.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tml.constant.MessageConstant.API_NOT_IMPLEMENTED;

/**
 * @NAME: CommentController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequestMapping("/communication/comment")
public class CommentController {


    /**
     * 获取某个帖子的评论列表
     */
    @GetMapping("/list")
    public Result list(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 评论帖子    (回复)  [T]  [审]
     */
    @GetMapping("/add")
    public Result add(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 点赞评论  [T]
     */
    @GetMapping("/favorite")
    public Result favorite(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


}