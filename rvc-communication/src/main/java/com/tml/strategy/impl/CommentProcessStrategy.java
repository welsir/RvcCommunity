package com.tml.strategy.impl;

import com.tml.mapper.CommentMapper;
import com.tml.mapper.CoverMapper;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.pojo.entity.Comment;
import com.tml.pojo.entity.Cover;
import com.tml.strategy.DetectionProcessStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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


        Comment comment = commentMapper.selectById(detectionStatusDto.getId());
        comment.setViolationInformation(detectionStatusDto.getViolationInformation());
        comment.setDetectionStatus(detectionStatusDto.getStatus());
        commentMapper.updateById(comment);

    }
}