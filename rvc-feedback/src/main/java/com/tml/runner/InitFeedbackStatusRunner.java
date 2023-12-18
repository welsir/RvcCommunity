package com.tml.runner;

import com.tml.service.FeedbackStatusService;
import com.tml.service.FeedbackTypeService;
import io.github.common.logger.CommonLogger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class InitFeedbackStatusRunner implements ApplicationRunner {

    @Resource
    FeedbackStatusService feedbackStatusService;

    @Resource
    CommonLogger commonLogger;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        commonLogger.info("正在初始化反馈服务状态列表...");
        feedbackStatusService.queryAll();
        commonLogger.info("初始化反馈服务状态列表完成！");
    }
}
