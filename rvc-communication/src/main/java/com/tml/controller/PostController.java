package com.tml.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.annotation.SystemLog;
import com.tml.mq.producer.handler.ProducerHandler;
import com.tml.pojo.dto.CoverDto;
import com.tml.pojo.dto.DetectionTaskDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Post;
import com.tml.pojo.vo.PostVo;
import com.tml.service.PostService;
import com.tml.utils.BeanUtils;
import io.github.common.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class PostController {


    @Autowired
    private PostService postService;

    @SystemLog(businessName = "获取交流帖子列表")
    @GetMapping("/list")
    public Result list(PageInfo<String> params){
        List<PostVo> PostListPage =postService.list(params);
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
     * 先不实现
     * @return
     */
    @GetMapping("/favorite")
    @SystemLog(businessName = "点赞帖子  [T]")
    public Result favorite(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 先不实现
     * @return
     */
    @GetMapping("/collection")
    @SystemLog(businessName = "收藏帖子 [T]")
    public Result collection(){
        return Result.success(API_NOT_IMPLEMENTED);
    }


    /**
     * 审核流程   借用接口测试音频
     * @return
     */
    @GetMapping("/add")
    @SystemLog(businessName = "发布帖子  [T]  [审]")
    public Result add(@RequestBody CoverDto coverDto){
        //        审核
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id("1111")
                .content(coverDto.getUrl())
                .name("audio")
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"audio");

        return Result.success(coverDto);
    }


    /**
     * 是否软删除
     * @return
     */
    @GetMapping("/delete")
    @SystemLog(businessName = "删除帖子   [T]")
    public Result delete(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    /**
     *
     * @return
     */
    @GetMapping("/update")
    @SystemLog(businessName = "修改我发布的帖子  [T]  [审]")
    public Result update(){
        return Result.success(API_NOT_IMPLEMENTED);
    }



    @GetMapping("/cover")
    @SystemLog(businessName = "上传帖子封面  [T]  [审]")
    public Result cover(@RequestBody CoverDto coverDto){
        //        审核
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id("1111")
                .content(coverDto.getUrl())
                .name("post")
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"image");

        return Result.success(coverDto);
    }

}