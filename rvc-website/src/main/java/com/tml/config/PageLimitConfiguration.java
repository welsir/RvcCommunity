package com.tml.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Data
public class PageLimitConfiguration {

    @Value("${rvc.limit.notice.home}")
    private Integer homeNoticeLimit = 5;

    @Value("${rvc.limit.notice.web}")
    private Integer webNoticeLimit = 10;
}
