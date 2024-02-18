package com.tml.controller;

import com.alibaba.fastjson.JSON;
import com.tml.annotation.apiAuth.LaxTokenApi;
import com.tml.aspect.annotation.SystemLog;
import com.tml.domain.dto.DetectionTaskDto;
import com.tml.domain.dto.DetectionTaskListDto;
import com.tml.utils.Uuid;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static com.tml.constant.DetectionConstants.DETECTION_EXCHANGE_NAME;


/**
 * @NAME: UnitTestController
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/communication/post/test")
@Slf4j
public class UnitTestController {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Resource
    Uuid uuid;


    @GetMapping("/uuid-test")
    @SystemLog(businessName = "UID测试")
    @LaxTokenApi
    public Result uid(String type) throws InterruptedException {
        Random random = new Random();
        switch (type){
            case "1": for (int i = 0; i < 10; i++) {
                System.out.println(Uuid.getUuid());
            };break;
            case "2":{
                for (int i = 0; i < 10; i++) {
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(random.nextInt(1000  ));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(Uuid.getUuid());

                        }
                    }.start();
                }
            }
        }

        return Result.success("ok");
    }



    @GetMapping("/mq-list-test")
    @SystemLog(businessName = "mq多任务测试")
    @LaxTokenApi
    public Result list(String type){
        DetectionTaskListDto detectionTaskListDto = new DetectionTaskListDto(new ArrayList<>());
        detectionTaskListDto.setSync( type.equals("1") ?true:false);
        setDetectionTaskDto(5,detectionTaskListDto);

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
        rabbitTemplate.convertAndSend(DETECTION_EXCHANGE_NAME,"detection.topic.list.key",JSON.toJSONString(detectionTaskListDto),cd);
        return Result.success("ok");
    }


    public static void setDetectionTaskDto(int nums, DetectionTaskListDto detectionTaskListDto){
        for (int i = 0; i < nums; i++) {
            DetectionTaskDto detectionTaskDto = new DetectionTaskDto();
            //设置参数，发布任务
            detectionTaskDto.setId(Uuid.getUuid());
            detectionTaskDto.setType("text");
            detectionTaskDto.setContent("队列测试消息" + i);
            detectionTaskDto.setRouterKey(detectionTaskListDto.isSync()?"res.topic.communication.comment.list.sync.key":"res.topic.communication.comment.list.key");
            detectionTaskListDto.getTaskList().add(detectionTaskDto);
        }
    }
}