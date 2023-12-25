package com.tml.common.strategy;

import com.tml.domain.DO.UserInfo;
import com.tml.domain.dto.DetectionStatusDto;

import java.time.LocalDateTime;

import static com.tml.config.DetectionConfig.*;

/**
 * @Date 2023/12/20
 * @Author xiaochun
 */
public class NicknameDetectionProcess extends UserDetectionStrategy {
    @Override
    public UserInfo process(DetectionStatusDto detectionStatusDto, UserInfo user, String content) {
        if (!detectionStatusDto.getLabels().equals(NON_LABEL)) {
            user.setNickname(RETURN);
            user.setUpdatedAt(LocalDateTime.now());
            return user;
        }
        user.setUpdatedAt(LocalDateTime.now());
        user.setNickname(content);
        return user;
    }
}
