package com.tml.strategy;

import com.tml.pojo.dto.DetectionStatusDto;

public interface DetectionProcessStrategy {
    void process(DetectionStatusDto detectionStatusDto);
}
