package com.tml.controller;


import com.tml.aspect.annotation.ContentDetection;
import com.tml.aspect.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.constant.enums.ContentDetectionEnum;
import com.tml.domain.dto.CoinDto;
import com.tml.domain.dto.CommentDto;
import com.tml.domain.dto.PageInfo;
import com.tml.service.CommentService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import static com.tml.constant.DetectionConstants.*;

/**
 * @NAME: CommentController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequestMapping("/communication/comment")
@RequiredArgsConstructor
@Validated
@SuppressWarnings({"all"})
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    @SystemLog(businessName = "获取某个帖子的评论列表")
    @LaxTokenApi
    public Result list(@Valid PageInfo<String> params,
                       @RequestHeader(required = false) String uid){
        return Result.success(commentService.list(uid, params.getData(),params.getPage(), params.getLimit(),"0"));
    }

    @GetMapping("/childrenList")
    @SystemLog(businessName = "获取某个帖子的子评论列表")
    @LaxTokenApi
    public Result childrenList(@Valid PageInfo<String> params,
                               @RequestHeader(required = false) String uid){
        return Result.success(commentService.childrenList(uid, params.getData(),params.getPage(), params.getLimit(),"0"));
    }

    @PostMapping("/add")
    @ContentDetection(routerKey = DETECTION_RES_COMMENT_KEY)
    @WhiteApi
    @SystemLog(businessName = "评论帖子    (回复)  [T]  [审]")
    public Result add(@RequestBody @Valid CommentDto commentDto,
                      @RequestHeader String uid){
        return Result.success(commentService.comment(commentDto,uid));
    }

    @PutMapping("/favorite")
    @SystemLog(businessName = "点赞评论  [T]")
    @WhiteApi
    public Result favorite(@RequestBody @Valid CoinDto coinDto,
                           @RequestHeader String uid){
        commentService.favorite(coinDto,uid);
        return Result.success();
    }
}