package com.tml.designpattern.strategy;

import com.tml.pojo.pojo.DetectionStatusDto;

public interface DetectionProcessStrategy {
    void process(DetectionStatusDto detectionStatusDto);
}
