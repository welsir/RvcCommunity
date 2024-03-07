package com.tml.designpattern.rule;

import com.alibaba.fastjson.JSONObject;
import com.tml.domain.dto.MqConsumerTaskDto;
import io.github.util.time.TimeUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * 次数限制规则，例如多少时间多少次
 */
@Component
@Scope("prototype")
public class FrequencyGradeRule extends AbstractGradeRule{

    //次数
    private Integer frequency;

    //天数
    private Integer day;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String getRuleId() {
        return "001";
    }

    @Override
    public String getRuleName() {
        return "次数规则";
    }

    @Override
    protected boolean ruleParser0(JSONObject originData) {
        day = originData.getInteger("day");
        frequency = originData.getInteger("frequency");
        return day!=null&&frequency!=null;
    }

    @Override
    public boolean check() {
        String taskId = taskDto.getPath();
        String uid = taskDto.getUserId();

        String key = String.format("grade:%s:freq:rule",taskId);
        Double score = stringRedisTemplate.opsForZSet().score(key, uid);
        return score==null||score<frequency;
    }

    @Override
    public void lastWord() {
        String taskId = taskDto.getPath();
        String uid = taskDto.getUserId();
        String key = String.format("grade:%s:freq:rule",taskId);
        stringRedisTemplate.opsForZSet().incrementScore(key,uid,1);
    }


}
