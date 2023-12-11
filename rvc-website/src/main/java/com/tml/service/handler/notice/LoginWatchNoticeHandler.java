package com.tml.service.handler.notice;

import com.tml.common.handler.HandlerStrategy;
import com.tml.constant.RedisKeyPool;
import com.tml.service.INoticeDaoService;
import com.tml.utils.StatusUtil;
import io.github.common.RedisKey;
import io.github.common.web.Result;
import io.github.pool.StatusPool;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.tml.constant.HandlerName.LOGIN_WATCH_NOTICE;

/**
 * 登录用户浏览公告策略
 */
@Component
public class LoginWatchNoticeHandler implements HandlerStrategy<Map<String,String>, Result> {

    @Resource
    INoticeDaoService noticeDaoService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String name() {
        return LOGIN_WATCH_NOTICE;
    }

    @Override
    public Result handlerRes(Map<String,String> map) {
        String uid = map.get("uid");
        String noticeId = map.get("noticeId");

        RedisKey watchKey = RedisKeyPool.WATCH_KEY;
        String key = watchKey.getKey(noticeId, uid);

        if(Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "1", watchKey.getTime(), watchKey.getTimeUnit()))){
            if (noticeDaoService.watchNotice(noticeId)) {
                return Result.success();
            }
            return Result.error(StatusUtil.ERROR_403("浏览失败"));
        }else{
            return Result.error(StatusUtil.ERROR_403("短时间内已经浏览过了"));
        }
    }
}
