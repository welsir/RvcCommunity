package com.tml.controller;

import com.tml.annotation.SystemLog;
import com.tml.pojo.dto.CoinDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.dto.PostDto;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
public class PostController {


    private final PostService postService;


    @SystemLog(businessName = "获取交流帖子列表")
    @GetMapping("/list")
    public Result list(PageInfo<String> params,@RequestParam("tagId") String tagId){
        List<PostVo> PostListPage =postService.list(params,tagId);
    return Result.success(PostListPage);
    }


    /**
     * 用户id如何获取
     * @return
     */
    @GetMapping("/details")
    @SystemLog(businessName = "获取某个帖子详情信息")
    public Result details(String postId){
        PostVo postVo = postService.details(postId);
        return Result.success(postVo);
    }


    /**
     * 点赞帖子
     * @return
     */
    @PutMapping("/favorite")
    @SystemLog(businessName = "点赞帖子  [T]")
    public Result favorite(@RequestBody CoinDto coinDto){
        postService.favorite(coinDto);
        return Result.success();
    }



    @PutMapping("/collection")
    @SystemLog(businessName = "收藏帖子 [T]")
    public Result collection(@RequestBody CoinDto coinDto){
        postService.collection(coinDto);
    return Result.success();
    }


    /**
     * 审核流程  用户先上传封面进行审核     发布帖子携带封面id
     * @return
     */
    @PostMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    public Result add(@RequestBody PostDto postDto){
        postService.add(postDto);
        return Result.success();
    }



    @DeleteMapping("/delete/{postId}")
    @SystemLog(businessName = "删除帖子   [T]")
    public Result delete(@PathVariable("postId") String postId){
        postService.delete(postId);
        return Result.success();
    }



    /**
     *修改我发布的帖子
     * @return
     */
    @PutMapping("/update")
    @SystemLog(businessName = "修改我发布的帖子  [T]  [审]")
    public Result update(@RequestBody PostDto postDto){
//        postService.update(postDto);
        return Result.success();
    }



    @GetMapping("/cover")
    @SystemLog(businessName = "上传帖子封面  [T]  [审]")
    public Result cover( String coverUrl){
        String coverId = postService.cover(coverUrl);
        return Result.success(coverId);
    }

}