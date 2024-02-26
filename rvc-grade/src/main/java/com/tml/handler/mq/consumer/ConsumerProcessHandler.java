package com.tml.handler.mq.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.designpattern.chain.ext.LastStepApproveChain;
import com.tml.designpattern.chain.ext.NumberApproveChain;
import com.tml.domain.dto.MqConsumerTaskDto;
import com.tml.domain.entity.Rule;
import com.tml.domain.entity.RvcLevelTask;
import com.tml.mapper.RvcLevelTaskMapper;
import com.tml.mapper.RvcLevelUserMapper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * @NAME: ConsumerProcessHandler
 * @USER: yuech
 * @Description:消费者处理消息
 * @DATE: 2024/2/21
 */
@Component
public class ConsumerProcessHandler {

    @Resource
    private RvcLevelTaskMapper rvcLevelTaskMapper;

//    @Resource
//    private RedisTemplate redisTemplate;
//
    @Resource
    private RvcLevelUserMapper rvcLevelUserMapper;

    @Resource
    private NumberApproveChain numberApproveChain;

    @Resource
    private LastStepApproveChain lastStepApproveChain;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "rvc.grade.consumer.queue"),
            exchange = @Exchange(name = "rvc.grade.consumer",type = ExchangeTypes.TOPIC),
            key = "rvc.grade.consumer.key"
    ))
    public void process(Message message) throws Exception {
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        MqConsumerTaskDto mqConsumerTaskDto = objectMapper.readValue(content, MqConsumerTaskDto.class);

        /// TODO: 2024/2/21 处理经验值增加的业务
        /**
         * 1、从数据库获取任务信息   去规则代码库解析
         * rule 为数据库中的规则
         */
        RvcLevelTask task = rvcLevelTaskMapper.getOne(new QueryWrapper<RvcLevelTask>().eq("task_url", mqConsumerTaskDto.getPath()));
//        JSONObject jsonObject = JSON.parseObject(task.getRule());
//        Rule rule = new Rule(jsonObject);

        /**
         * 2、进行校验
         * 2.1 时间直接判断   次数-> 每一个任务对应一个 zset  完成次数就是分数值
         *
         */


        /**todo
         * 校验使用责任链  传rule对象 根据rule对象的属性值来判断是否需要校验
         */
        numberApproveChain.setNext(mqConsumerTaskDto,task,lastStepApproveChain);
        numberApproveChain.approve();
//        if (numberApproveChain.approve()){
//            processingResult(task,mqConsumerTaskDto);
//        }

    }

    /**
     * 处理结果  ： 给用户增加等级
     * 需要处理的内容：1、如果通过次数校验 redis 中set得分增加
     *              2、数据库中用户经验增加
     *
     * @param task
     * @param taskDto
     */
//    private void processingResult(RvcLevelTask task, MqConsumerTaskDto taskDto) {
//        String uid = taskDto.getUserId();
//        Rule rule = Rule.getRule(task);
//        //redis中set得分增加
//        if (!Objects.isNull(rule.getNumberRule())){
//            zSetOps.add("rvc:grade:task:" + task.getId(), mqConsumerTaskDto.getUserId(), 0.0);
//        }
//
//
//
//
//    }

//    private boolean check(RvcLevelTask task, MqConsumerTaskDto mqConsumerTaskDto, Rule rule, ZSetOperations<String, String> zSetOps) {
//        if (checkNumber(task, mqConsumerTaskDto, rule,zSetOps)) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkNumber(RvcLevelTask task, MqConsumerTaskDto mqConsumerTaskDto, Rule rule,ZSetOperations<String, String> zSetOps) {
//        /**
//         * 次数判断
//         */
//        //如果不存在先初始化
//        Double score = zSetOps.score("rvc:grade:task:" + task.getId(), mqConsumerTaskDto.getUserId());
//        if (Objects.isNull(score)){
//            zSetOps.add("rvc:grade:task:" + task.getId(), mqConsumerTaskDto.getUserId(), 0.0);
//            score = 0.0;
//        }
//        // 监视有序集合的键
//        redisTemplate.watch("rvc:grade:task:" + task.getId());
//
//        if (rule.getNumberRule() > score.intValue()){
//            // 增加指定成员的分数
//            return true;
//        }
//        return false;
//    }
}