package com.tml.config;

import com.tml.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeConfig {

    private long workerId;

    private long dataCenterId;

    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator(){
        return new SnowflakeIdGenerator(workerId,dataCenterId);
    }
}
