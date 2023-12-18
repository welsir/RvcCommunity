package com.tml.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 15:22
 */
@Data
@Configuration
@Component
public class LoggerConfig {

    @Value("${file.logger.handler}")
    private String logger;

    @Value("${file.logger.enable}")
    private boolean enable;

}
