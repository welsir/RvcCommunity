package com.tml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean("asyncMaster")
    public ThreadPoolTaskExecutor asyncMaster() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(1024); // 队列大小
        executor.setThreadNamePrefix("asyncMaster-"); // 线程名前缀
        executor.initialize();
        return executor;
    }
}
