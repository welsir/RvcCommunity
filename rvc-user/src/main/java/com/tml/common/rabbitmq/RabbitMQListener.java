package com.tml.common.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.tml.common.strategy.AavatarDetectionProcess;
import com.tml.common.strategy.DescriptionDetectionProcess;
import com.tml.common.strategy.NicknameDetectionProcess;
import com.tml.common.strategy.UserDetectionStrategy;
import com.tml.config.DetectionConfig;
import com.tml.mapper.UserInfoMapper;
import com.tml.mq.ReceiveHandler;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.dto.DetectionTaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tml.config.DetectionConfig.*;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Component
public class RabbitMQListener extends ReceiveHandler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserInfoMapper userInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserDetectionStrategy.class);

    public RabbitMQListener() {
        STEATEGY.put(DetectionConfig.USER_AVATAR, new AavatarDetectionProcess());
        STEATEGY.put(DetectionConfig.USER_NICKNAME, new NicknameDetectionProcess());
        STEATEGY.put(DetectionConfig.USER_DESCRIPTION, new DescriptionDetectionProcess());
    }

    @Override
    public void process(DetectionStatusDto detectionStatusDto) {
        UserDetectionStrategy userDetectionStrategy = STEATEGY.get(detectionStatusDto.getName());
        if(userDetectionStrategy == null){
            return;
        }
        logger.info("审核结果：" + detectionStatusDto.getId() + " " + detectionStatusDto.getLabels());
        String name = detectionStatusDto.getName();
        String id = detectionStatusDto.getId();
        String content = stringRedisTemplate.opsForValue().get(BASE + name + id);
        UserInfo user = userDetectionStrategy.process(detectionStatusDto, userInfoMapper.selectById(id), content);
        userInfoMapper.updateById(user);
        stringRedisTemplate.delete(BASE + name + id);
    }

    public void submit(DetectionTaskDTO task, String type){
        rabbitTemplate.convertAndSend(EXCHANGE, BASE_ROUTING_KEY + type, JSON.toJSONString(task));
        stringRedisTemplate.opsForValue().set(BASE + task.getName() + task.getId(), task.getContent());
    }
}
