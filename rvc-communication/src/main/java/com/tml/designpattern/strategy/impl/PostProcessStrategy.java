package com.tml.designpattern.strategy.impl;

import com.tml.mapper.PostMapper;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.entity.Post;
import com.tml.designpattern.strategy.DetectionProcessStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @NAME: PostProcessStrategy
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/9
 */
@Component
@RequiredArgsConstructor
public class PostProcessStrategy implements DetectionProcessStrategy {

    private final PostMapper postMapper;
    @Override
    public void process(DetectionStatusDto detectionStatusDto) {
        Post post =  postMapper.selectById(detectionStatusDto.getId());
        post.setLabels(detectionStatusDto.getLabels());
        if (detectionStatusDto.getLabels().equals("nonLabel")){
            post.setDetectionStatus(1);
        }else{
            post.setDetectionStatus(2);
        }
        postMapper.updateById(post);
    }
}