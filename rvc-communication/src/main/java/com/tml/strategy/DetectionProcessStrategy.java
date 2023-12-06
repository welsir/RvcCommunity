package com.tml.strategy;

import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.vo.PostVo;

import java.util.List;

public interface DetectionProcessStrategy {
    void process(DetectionStatusDto detectionStatusDto);
}
