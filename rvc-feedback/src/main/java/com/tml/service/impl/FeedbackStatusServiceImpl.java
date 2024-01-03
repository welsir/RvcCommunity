package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.constant.RedisKeyPool;
import com.tml.mapper.FeedbackStatusMapper;
import com.tml.pojo.FeedbackStatusDO;
import com.tml.service.FeedbackStatusService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackStatusServiceImpl implements FeedbackStatusService {

    @Resource
    private FeedbackStatusMapper mapper;

    @Resource
    private ThreadPoolTaskExecutor asyncMaster;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<FeedbackStatusDO> queryAll() {
        String redisKey = RedisKeyPool.HASH_STATUS_TYPE.getKey();
        List<FeedbackStatusDO> statusDOS;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(redisKey);

        if(entries.isEmpty()){
            statusDOS = mapper.selectList(new QueryWrapper<>());
            //异步缓存
            List<FeedbackStatusDO> finalStatusDOS = statusDOS;
            asyncMaster.submit(()->{
                stringRedisTemplate.opsForHash().putAll(redisKey,
                        finalStatusDOS.stream().collect(
                                Collectors.toMap(fb-> fb.getId().toString(), FeedbackStatusDO::getStatus))
                );
            });
        }else{
            statusDOS = entries.entrySet().stream()
                    .map(entry -> new FeedbackStatusDO( Integer.valueOf(entry.getKey().toString()), (String) entry.getValue()))
                    .collect(Collectors.toList());
        }

        return statusDOS;
    }
}
