package com.tml.designpattern.strategy;

import com.tml.domain.dto.DetectionStatusDto;

public interface DetectionProcessStrategy {
    void process(DetectionStatusDto detectionStatusDto);
}
