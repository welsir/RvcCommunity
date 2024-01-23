package com.tml.designpattern.strategy;

import com.tml.pojo.dto.DetectionStatusDto;

public interface DetectionProcessStrategy {
    void process(DetectionStatusDto detectionStatusDto);
}
