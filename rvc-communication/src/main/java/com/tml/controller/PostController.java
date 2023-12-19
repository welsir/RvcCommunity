package com.tml.controller;

import com.tml.annotation.ContentDetection;
import com.tml.annotation.SystemLog;
import com.tml.enums.ContentDetectionEnum;
import com.tml.feign.communication.RvcCommunicationServiceFeignClient;
import com.tml.interceptor.UserLoginInterceptor;
import com.tml.pojo.dto.*;
import com.tml.pojo.vo.PostSimpleVo;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

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
    public Result list(@Valid PageInfo<String> params, @RequestParam("tagId") String tagId){
        List<PostVo> PostListPage =postService.list(params,tagId);
    return Result.success(PostListPage);
    }


    @GetMapping("/details")
    @SystemLog(businessName = "获取某个帖子详情信息")
    public Result details(@RequestParam @NotBlank String postId){
        PostVo postVo = postService.details(postId);
        return Result.success(postVo);
    }


    /**
     * 点赞帖子
     * @return
     */
    @PutMapping("/favorite")
    @SystemLog(businessName = "点赞帖子  [T]")
    public Result favorite(@RequestBody
                               @Valid CoinDto coinDto){
        postService.favorite(coinDto);
        return Result.success();
    }



    @PutMapping("/collection")
    @SystemLog(businessName = "收藏帖子 [T]")
    public Result collection(@RequestBody
                                 @Valid CoinDto coinDto){
        postService.collection(coinDto);
    return Result.success();
    }


    @PostMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    @ContentDetection(type = ContentDetectionEnum.POST_CONTENT,exchangeName = DETECTION_EXCHANGE_NAME)
    public Result add(@RequestBody
                          @Valid PostDto postDto){
        String postId = postService.add(postDto);
        return Result.success(postId);
    }



    @DeleteMapping("/delete/{postId}")
    @SystemLog(businessName = "删除帖子   [T]")
    public Result delete(@PathVariable("postId") @NotBlank String postId){
        postService.delete(postId);
        return Result.success();
    }



    @GetMapping("/user/favorite")
    @SystemLog(businessName = "获取用户点赞的贴子")
    public Result userFavorite(
            @Valid PageInfo<String> params){
        List<PostSimpleVo> postVoListPage = postService.userFavorite(params);
        return Result.success(postVoListPage);
    }

    @GetMapping("/user/collect")
    @SystemLog(businessName = "获取用户收藏的贴子")
    public Result userCollect(
            @Valid PageInfo<String> params){
        List<PostSimpleVo> postVoListPage = postService.userCollect(params);
        return Result.success(postVoListPage);
    }

    @GetMapping("/user/create")
    @SystemLog(businessName = "获取用户创建的贴子")
    public Result userCreate(
            @Valid PageInfo<String> params){
        List<PostSimpleVo> postVoListPage = postService.userCreate(params);
        return Result.success(postVoListPage);
    }


    //用户上传头像
    //文件上传
    @PostMapping("/cover")
    @SystemLog(businessName = "用户上传头像  文件上传")
    public Result setUserProfile(@RequestPart("wangeditor-uploaded-image") MultipartFile profile) throws IOException {
        //从header获取用户id
        LoginInfoDTO loginInfoDTO = UserLoginInterceptor.loginUser.get();
        String url = postService.updUserProfile(profile);
        CoverDto build = CoverDto.builder()
                .uid(loginInfoDTO.getId())
                .coverUrl(url)
                .build();
        rvcCommunicationServiceFeignClient.coverUrl(build);
        return Result.success(url);
    }


    //上传图片
    //链接上传
    @PostMapping("/coverUrl")
    @SystemLog(businessName = "用户上传头像  url上传")
    @ContentDetection(type = ContentDetectionEnum.POST_COVER,exchangeName = DETECTION_EXCHANGE_NAME)
    public Result coverUrl(@RequestBody CoverDto coverDto) {
        String urlId =  postService.coverUrl(coverDto);
        return Result.success(urlId);
    }

}