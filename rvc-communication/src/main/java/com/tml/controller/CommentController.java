package com.tml.controller;


import com.tml.annotation.ContentDetection;
import com.tml.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.enums.ContentDetectionEnum;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.vo.CommentVo;
import com.tml.service.CommentService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import static com.tml.constant.DetectionConstants.DETECTION_EXCHANGE_NAME;

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
    @LaxTokenApi
    public Result list(@Valid PageInfo<String> params){
        return Result.success(commentService.list(params));
    }

    @PostMapping("/add")
    @ContentDetection(type = ContentDetectionEnum.COMMENT,exchangeName = DETECTION_EXCHANGE_NAME)
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