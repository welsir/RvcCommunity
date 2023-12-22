package com.tml.controller;

import com.tml.aspect.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.service.PostTypeService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @NAME: PostTypeController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/communication/post/type")
public class PostTypeController {

    private final PostTypeService postTypeService;
    @GetMapping("/list")
    @SystemLog(businessName = "获取所有交流类型列表")
    @LaxTokenApi
    public Result list(){
        return Result.success(postTypeService.listType());
    }
}