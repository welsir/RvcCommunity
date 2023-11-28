package com.tml.service.handler.notice;


import com.tml.common.handler.HandlerStrategy;
import io.github.common.web.Result;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.tml.constant.HandlerName.NO_LOGIN_WATCH_NOTICE;

/**
 * 未登录用户浏览公告策略
 */
@Component
public class WatchNoticeHandler implements HandlerStrategy<Map<String,String>, Result> {
    @Override
    public String name() {
        return NO_LOGIN_WATCH_NOTICE;
    }

    @Override
    public Result handlerRes(Map<String, String> map) {
        return Result.success();
    }
}
