package com.tml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RvcChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RvcChatApplication.class, args);
    }

}
