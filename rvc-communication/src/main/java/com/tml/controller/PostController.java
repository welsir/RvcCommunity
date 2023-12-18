package com.tml.controller;

import com.tml.annotation.SystemLog;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.dto.PostDto;
import com.tml.pojo.vo.CommentVo;
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
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

import static com.tml.constant.MessageConstant.API_NOT_IMPLEMENTED;

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


    @SystemLog(businessName = "获取交流帖子列表")
    @GetMapping("/list")
    public Result list(@Valid PageInfo<String> params, @RequestParam("tagId") String tagId){
        List<PostVo> PostListPage =postService.list(params,tagId);
    return Result.success(PostListPage);
    }


    /**
     * 用户id如何获取
     * @return
     */
    @GetMapping("/details")
    @SystemLog(businessName = "获取某个帖子详情信息")
    public Result details(@Valid @NotBlank String postId){
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


    /**
     * 审核流程  用户先上传封面进行审核     发布帖子携带封面id
     * @return
     */
    @PostMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    public Result add(@RequestBody
                          @Valid PostDto postDto){
        postService.add(postDto);
        return Result.success();
    }



    @DeleteMapping("/delete/{postId}")
    @SystemLog(businessName = "删除帖子   [T]")
    public Result delete(@PathVariable("postId") @Valid @NotBlank String postId){
        postService.delete(postId);
        return Result.success();
    }



    /**
     *修改我发布的帖子
     * @return
     */
//    @PutMapping("/update")
//    @SystemLog(businessName = "修改我发布的帖子  [T]  [审]")
//    public Result update(@RequestBody PostDto postDto){
//        postService.update(postDto);
//        return Result.success();
//    }



//    @GetMapping("/cover")
//    @SystemLog(businessName = "上传帖子封面  [T]  [审]")
//    public Result cover(  @Valid @NotBlank String coverUrl){
//        String coverId = postService.cover(coverUrl);
//        return Result.success(coverId);
//    }



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
    //富文本上传图片
    @PostMapping("/cover")
    public Result setUserProfile(@RequestParam("wangeditor-uploaded-image") MultipartFile profile) throws IOException {
        postService.updUserProfile(profile);
        return Result.success();
    }


}