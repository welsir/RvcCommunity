package com.tml.config;

import com.tml.common.strategy.UserDetectionStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2023/12/19
 * @Author xiaochun
 */
public class DetectionConfig {
    public static final String BASE = "rvc:user:detection:";

    public static final String USER_NICKNAME = "nickname:";

    public static final String USER_DESCRIPTION = "description:";

    public static final String USER_AVATAR = "avatar:";

    public static final String EXCHANGE = "detection.topic";

    public static final String BASE_ROUTING_KEY = "detection.";

    public static final String NON_LABEL = "nonLabel";

    public static Map<String, UserDetectionStrategy> STEATEGY = new HashMap<>();

    public static final String RETURN = "发现违规内容，审核不通过";
}
