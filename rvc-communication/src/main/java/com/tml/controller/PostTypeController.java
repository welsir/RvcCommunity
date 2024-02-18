package com.tml.controller;

import com.alibaba.fastjson.JSON;
import com.tml.aspect.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.domain.dto.DetectionTaskDto;
import com.tml.service.PostTypeService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.tml.constant.DetectionConstants.DETECTION_EXCHANGE_NAME;
import static com.tml.constant.DetectionConstants.DETECTION_ROUTER_KEY_HEADER;

/**
 * @NAME: PostTypeController
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/communication/post/type")
@Slf4j
@SuppressWarnings({"all"})
public class PostTypeController {

    private final PostTypeService postTypeService;

    @GetMapping("/list")
    @SystemLog(businessName = "获取所有交流类型列表")
    @LaxTokenApi
    public Result list(){
        return Result.success(postTypeService.listType());
    }


    @Resource
    RabbitTemplate rabbitTemplate;
    @GetMapping("/hello")
    @SystemLog(businessName = "发布hello 任务")
    public void hello(){
        String type = "text";
        String content = "hello word";
        DetectionTaskDto detectionTaskDto = new DetectionTaskDto();
        //自己服务对应的路由key
        detectionTaskDto.setRouterKey("res.topic.communication.hello.key");
        detectionTaskDto.setType("text");
        detectionTaskDto.setId("1");
        detectionTaskDto.setContent(content);
        //1、审核服务交换机   2、审核对应服务的路由   3、审核内容
        rabbitTemplate.convertAndSend("detection.topic", "detection.topic.key", JSON.toJSONString(detectionTaskDto));
    }
}