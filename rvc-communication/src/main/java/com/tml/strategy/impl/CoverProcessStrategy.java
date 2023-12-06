package com.tml.strategy.impl;

import com.tml.mapper.CoverMapper;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.entity.Cover;
import com.tml.strategy.DetectionProcessStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CoverProcessStrategy implements DetectionProcessStrategy {

     private final CoverMapper coverMapper;

    @Override
     public void process(DetectionStatusDto detectionStatusDto) {
        Cover cover = coverMapper.selectById(detectionStatusDto.getId());
        cover.setViolationInformation(detectionStatusDto.getViolationInformation());
        cover.setHasShow(detectionStatusDto.getStatus());
        coverMapper.updateById(cover);
    }
}