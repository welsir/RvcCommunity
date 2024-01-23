package com.tml.common.strategy;

import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.dto.DetectionStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Date 2023/12/20
 * @Author xiaochun
 */
public abstract class UserDetectionStrategy {
    public abstract UserInfo process(DetectionStatusDto detectionStatusDto, UserInfo user, String content);
}
