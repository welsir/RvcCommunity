package com.tml.constant;

import io.github.common.RedisKey;

import java.util.concurrent.TimeUnit;

public class RedisKeyPool {
    /**
     * 公告浏览 redis key
     * noticeId,uid
     */
    public static final RedisKey WATCH_KEY = new RedisKey("rvc:website:watch:%s:%s",5, TimeUnit.MINUTES);
}
