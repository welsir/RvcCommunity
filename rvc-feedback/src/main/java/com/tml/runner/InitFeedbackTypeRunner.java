package com.tml.runner;

import com.tml.service.FeedbackTypeService;
import io.github.common.logger.CommonLogger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class InitFeedbackTypeRunner implements ApplicationRunner {

    @Resource
    FeedbackTypeService feedbackTypeService;

    @Resource
    CommonLogger commonLogger;

    @Override
    public void run(ApplicationArguments args) {
        commonLogger.info("正在初始化反馈服务类型...");
        feedbackTypeService.queryAll();
        commonLogger.info("初始化反馈服务类型完成！");
    }
}
