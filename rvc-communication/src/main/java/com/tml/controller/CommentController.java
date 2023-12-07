package com.tml.controller;


import com.tml.annotation.Detection;
import com.tml.annotation.SystemLog;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.CommentStatusDto;
import com.tml.service.CommentService;
import io.github.common.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private CommentService commentService;


    @GetMapping("/list")
    @SystemLog(businessName = "获取某个帖子的评论列表")
    public Result list(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @PostMapping("/add")
    @SystemLog(businessName = "评论帖子    (回复)  [T]  [审]")
    public Result add(@RequestBody CommentDto commentDto){
        commentService.comment(commentDto);
        return Result.success();
    }


    @PostMapping("/status")
    @SystemLog(businessName = "评论帖子的回调")
    public void status(@RequestBody CommentStatusDto commentStatusDto){
        commentService.status(commentStatusDto);
    }



    @GetMapping("/favorite")
    @SystemLog(businessName = "点赞评论  [T]")
    public Result favorite(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


}