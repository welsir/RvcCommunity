package com.tml.common.strategy;

import com.tml.config.DetectionConfig;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.dto.DetectionStatusDto;

import static com.tml.config.DetectionConfig.*;

/**
 * @Date 2023/12/20
 * @Author xiaochun
 */
public abstract class UserDetectionStrategy {
    public abstract UserInfo process(DetectionStatusDto detectionStatusDto, UserInfo user, String content);
}
