package com.tml;

import com.tml.config.QueryParamGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.tml.*","io.github.*"})
public class WebsiteApplication {
    public static void main( String[] args )
    {
        SpringApplication.run(WebsiteApplication.class, args);
    }
}
