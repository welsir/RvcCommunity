package com.tml.controller;

import com.alibaba.fastjson.JSON;
import com.tml.aspect.annotation.SystemLog;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.domain.dto.DetectionTaskDto;
import com.tml.service.PostTypeService;
import io.github.common.web.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.tml.constant.DetectionConstants.*;

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



        // 1.创建CorrelationData
        CorrelationData cd = new CorrelationData();
        // 2.给Future添加ConfirmCallback
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                // 2.1.Future发生异常时的处理逻辑，基本不会触发
                log.error("send message fail", ex);
            }
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                // 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
                if(result.isAck()){ // result.isAck()，boolean类型，true代表ack回执，false 代表 nack回执
                    log.debug("发送消息成功，收到 ack!");
                }else{ // result.getReason()，String类型，返回nack时的异常描述
                    log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
                }
            }
        });
        rabbitTemplate.convertAndSend("detection.topic", "detection.topic.key", JSON.toJSONString(detectionTaskDto),cd);

    }
}