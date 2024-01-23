package com.tml.controller;

import com.tml.aspect.annotation.ContentDetection;
import com.tml.aspect.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.enums.ContentDetectionEnum;

import com.tml.feign.RvcCommunicationServiceFeignClient;
import com.tml.pojo.dto.*;
import com.tml.service.PostService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;

import static com.tml.constant.DetectionConstants.DETECTION_EXCHANGE_NAME;

/**
 * @NAME: PostController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequestMapping("/communication/post")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final RvcCommunicationServiceFeignClient rvcCommunicationServiceFeignClient;
    @SystemLog(businessName = "获取交流帖子列表")
    @GetMapping("/list")
    @LaxTokenApi
    public Result list(@Valid PageInfo<String> params,
                       @RequestParam("tagId") String tagId,
                       @RequestHeader(required = false) String uid){
    return Result.success(postService.list(params,tagId,uid));
    }

    @GetMapping("/details")
    @SystemLog(businessName = "获取某个帖子详情信息")
    @LaxTokenApi
    public Result details(@RequestParam @NotBlank String postId,
                          @RequestHeader(required = false) String uid){
        return Result.success(postService.details(postId,uid));
    }

    /**
     * 点赞帖子
     * @return
     */
    @PutMapping("/favorite")
    @SystemLog(businessName = "点赞帖子  [T]")
    @WhiteApi
    public Result favorite(@RequestBody @Valid CoinDto coinDto,
                           @RequestHeader String uid){
        postService.favorite(coinDto,uid);
        return Result.success();
    }

    @PutMapping("/collection")
    @SystemLog(businessName = "收藏帖子 [T]")
    @WhiteApi
    public Result collection(@RequestBody @Valid CoinDto coinDto,
                             @RequestHeader String uid){
        postService.collection(coinDto,uid);
        return Result.success();
    }

    @PostMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    @ContentDetection(type = ContentDetectionEnum.POST_CONTENT,exchangeName = DETECTION_EXCHANGE_NAME)
    @WhiteApi
    public Result add(@RequestBody @Valid PostDto postDto,
                      @RequestHeader String uid){
        return Result.success(postService.add(postDto,uid));
    }

    @DeleteMapping("/delete")
    @SystemLog(businessName = "删除帖子   [T]")
    @WhiteApi
    public Result delete(@RequestParam("postId") @NotBlank String postId,
                         @RequestHeader String uid){
        postService.delete(postId,uid);
        return Result.success();
    }

    @GetMapping("/user/favorite")
    @SystemLog(businessName = "获取用户点赞的贴子")
    @WhiteApi
    public Result userFavorite(@Valid PageInfo<String> params,
                               @RequestHeader String uid){
        return Result.success(postService.userFavorite(params,uid));
    }

    @GetMapping("/user/collect")
    @SystemLog(businessName = "获取用户收藏的贴子")
    @WhiteApi
    public Result userCollect(@Valid PageInfo<String> params,
                              @RequestHeader String uid){
        return Result.success( postService.userCollect(params,uid));
    }

    @GetMapping("/user/create")
    @SystemLog(businessName = "获取用户创建的贴子")
    @WhiteApi
    public Result userCreate(@Valid PageInfo<String> params,
                             @RequestHeader String uid){
        return Result.success( postService.userCreate(params,uid));
    }

    @PostMapping("/cover")
    @SystemLog(businessName = "用户上传头像  文件上传")
    @WhiteApi
    public Result setUserProfile(@RequestPart("wangeditor-uploaded-image") MultipartFile profile,
                                 @RequestHeader String uid) throws IOException {
        String url = postService.updUserProfile(profile,uid);
        CoverDto build = CoverDto.builder()
                .coverUrl(url)
                .uid(uid)
                .build();
        Result result = rvcCommunicationServiceFeignClient.coverUrl(build);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", (String) result.getData());
        stringStringHashMap.put("url",url);

        return Result.success(stringStringHashMap);
    }

    @PostMapping("/coverUrl")
    @SystemLog(businessName = "用户上传头像  url上传")
    @LaxTokenApi
    @ContentDetection(type = ContentDetectionEnum.POST_COVER,exchangeName = DETECTION_EXCHANGE_NAME)
    public Result coverUrl(@RequestBody CoverDto coverDto) {
        return Result.success(postService.coverUrl(coverDto));
    }
}