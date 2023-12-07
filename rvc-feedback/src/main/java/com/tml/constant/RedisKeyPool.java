package com.tml.constant;

import io.github.common.RedisKey;

import java.util.concurrent.TimeUnit;

public class RedisKeyPool {
    /**
     * 公告浏览 redis key
     * noticeId,uid
     */
    public static final RedisKey HASH_FEEDBACK_TYPE = new RedisKey("rvc:feedback:type",-1,TimeUnit.MINUTES);
    public static final RedisKey HASH_STATUS_TYPE = new RedisKey("rvc:feedback:status",-1,TimeUnit.MINUTES);
}
