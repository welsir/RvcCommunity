package com.tml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Hello world!
 *
 */
@EnableFeignClients
@SpringBootApplication
@EnableAsync
public class App
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
}
