package com.tml.controller;

import com.tml.pojo.entity.PostTypeDo;
import com.tml.service.PostTypeService;
import io.github.common.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.tml.constant.MessageConstant.API_NOT_IMPLEMENTED;

/**
 * @NAME: PostTypeController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequestMapping("/communication/post/type")
public class PostTypeController {

    @Autowired
    private PostTypeService postTypeService;

    /**
     * 获取所有交流类型列表
     */
    @GetMapping("/list")
    public Result list(){
        List<PostTypeDo> list = postTypeService.list();
        return Result.success(list);
    }




}