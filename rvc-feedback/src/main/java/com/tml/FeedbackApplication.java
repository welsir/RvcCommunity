package com.tml;


import com.tml.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(defaultConfiguration = FeignConfig.class)
@SpringBootApplication
@EnableDiscoveryClient
public class FeedbackApplication {

    public static void main( String[] args )
    {
        SpringApplication.run(FeedbackApplication.class, args);
    }
}
