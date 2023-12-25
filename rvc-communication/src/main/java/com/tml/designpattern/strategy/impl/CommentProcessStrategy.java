package com.tml.designpattern.strategy.impl;

import com.tml.designpattern.strategy.DetectionProcessStrategy;
import com.tml.mapper.CommentMapper;
import com.tml.domain.dto.DetectionStatusDto;
import com.tml.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @NAME: CommentProcessStrategy
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/6
 */
@RequiredArgsConstructor
@Component
public class CommentProcessStrategy implements DetectionProcessStrategy {

    private final CommentMapper commentMapper;
    @Override
    public void process(DetectionStatusDto detectionStatusDto) {
        Comment comment =  commentMapper.selectById(detectionStatusDto.getId());
        comment.setLabels(detectionStatusDto.getLabels());
        if (detectionStatusDto.getLabels().equals("nonLabel")){
            comment.setDetectionStatus(1);
        }else{
            comment.setDetectionStatus(2);
        }

        commentMapper.updateById(comment);

    }
}