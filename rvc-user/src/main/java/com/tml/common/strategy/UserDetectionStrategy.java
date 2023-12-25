package com.tml.common.strategy;

import com.tml.domain.DO.UserInfo;
import com.tml.domain.dto.DetectionStatusDto;

/**
 * @Date 2023/12/20
 * @Author xiaochun
 */
public abstract class UserDetectionStrategy {
    public abstract UserInfo process(DetectionStatusDto detectionStatusDto, UserInfo user, String content);
}
