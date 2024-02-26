package com.tml.service.impl;

import com.tml.constant.RedisKeyPool;
import com.tml.pojo.FeedbackTypeDO;
import com.tml.service.FeedbackTypeService;
import io.github.common.logger.CommonLogger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackTypeServiceImpl implements FeedbackTypeService {

    @Resource
    private IFeedbackTypeDaoServiceImpl daoService;

    @Resource
    private ThreadPoolTaskExecutor asyncMaster;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    CommonLogger logger;

    @Override
    public FeedbackTypeDO hasType(Integer id) {
        String redisKey = RedisKeyPool.HASH_FEEDBACK_TYPE.getKey();
        FeedbackTypeDO feedbackTypeDO = null;
        String type = (String) Optional
                .ofNullable(stringRedisTemplate.opsForHash().get(redisKey, id.toString()))
                .orElse("");
        if (StringUtils.hasText(type)) {
            feedbackTypeDO = new FeedbackTypeDO();
            feedbackTypeDO.setId(id);
            feedbackTypeDO.setType(type);
        }
        return feedbackTypeDO;
    }

    @Override
    public List<FeedbackTypeDO> queryAll() {

        long startTime = System.currentTimeMillis();

        String redisKey = RedisKeyPool.HASH_FEEDBACK_TYPE.getKey();
        List<FeedbackTypeDO> typeDOS;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(redisKey);

        if(entries.isEmpty()){
            typeDOS = daoService.list();
            //异步缓存
            List<FeedbackTypeDO> finalTypeDOS = typeDOS;
            asyncMaster.submit(()->{
                stringRedisTemplate.opsForHash().putAll(redisKey,
                        finalTypeDOS.stream().collect(
                                Collectors.toMap(fb-> fb.getId().toString(), FeedbackTypeDO::getType))
                );
            });
        }else{
            typeDOS = entries.entrySet().stream()
                    .map(entry -> new FeedbackTypeDO( Integer.valueOf(entry.getKey().toString()), (String) entry.getValue()))
                    .collect(Collectors.toList());
        }

        long endTime = System.currentTimeMillis();

        logger.info("该接口耗时: %s ms",endTime-startTime);
        return typeDOS;
    }
}
