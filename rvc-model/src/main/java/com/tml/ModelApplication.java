package com.tml;

import com.tml.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients(defaultConfiguration = FeignConfig.class)
@SpringBootApplication
@EnableAsync
@MapperScan("com.tml.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class ModelApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ModelApplication.class, args);
    }
}
