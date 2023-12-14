package com.tml.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.annotation.SystemLog;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.vo.CommentVo;
import com.tml.service.CommentService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.tml.constant.MessageConstant.API_NOT_IMPLEMENTED;

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
public class CommentController {

    private final CommentService commentService;


    @GetMapping("/list")
    @SystemLog(businessName = "获取某个帖子的评论列表")
    public Result list(
            @Valid PageInfo<String> params){
        List<CommentVo> commentListPage = commentService.list(params);
        return Result.success(commentListPage);
    }



    @PostMapping("/add")
    @SystemLog(businessName = "评论帖子    (回复)  [T]  [审]")
    public Result add(@RequestBody
                          @Valid CommentDto commentDto){
        commentService.comment(commentDto);
        return Result.success();
    }



    @PostMapping("/favorite")
    @SystemLog(businessName = "点赞评论  [T]")
    public Result favorite(@RequestBody
                               @Valid CoinDto coinDto){
        commentService.favorite(coinDto);
        return Result.success();
    }


}