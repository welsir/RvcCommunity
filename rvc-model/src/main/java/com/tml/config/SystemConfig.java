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
    private String pageSize;

    @Value("${file.image.image-file-size}")
    private String imageSize;

    @Value("${labels.allow_labels}")
    private String[] allowLabels;

    @Value("${page.first-comment-size}")
    private String firstCommentLimit;

    @Value("${page.second-comment-size}")
    private String secondCommentLimit;

    @Value("${file.image.image-type}")
    private String[] imageType;

//    @Value("${file.model.model-type}")
//    private String[] modelType;

    @Value("${file.model.model-file-size}")
    private String modelFileSize;
}
