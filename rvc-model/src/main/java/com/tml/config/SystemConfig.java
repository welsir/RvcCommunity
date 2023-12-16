package com.tml.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/10 21:16
 */
@Configuration
@Data
@Component
public class SystemConfig {

    @Value("${page.size}")
    private String size;

    @Value("${file.image-file-size}")
    private String imageSize;

    @Value("${labels.allow_labels}")
    private String[] allowLabels;



    @PostConstruct
    public void init(){




    }

}
