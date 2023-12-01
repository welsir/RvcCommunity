package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.CommentMapper;
import com.tml.mq.producer.handler.ProducerHandler;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.CommentStatusDto;
import com.tml.pojo.dto.DetectionTaskDto;
import com.tml.pojo.entity.CommentDo;
import com.tml.service.CommentService;
import com.tml.utils.BeanUtils;
import com.tml.utils.Uuid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.tml.constant.CallbackUrlConstant.CALLBACK_URL_COMMENT;
import static com.tml.constant.DetectionConstants.STATUS_UNDERREVIEW;


/**
 * @NAME: CommentServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/30
 */
@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, CommentDo> implements CommentService {
    @Override
    public void comment(CommentDto commentDto) {
//        保存用户评论信息
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        Long uuid = Uuid.getUuid();
        CommentDo commentDo = CommentDo.builder()
                .postCommentId(uuid)
                .content(commentDto.getContent())
                .hasShow(STATUS_UNDERREVIEW)
                .userId(commentDto.getUserId())
                .postId(commentDto.getPostId())
                .rootCommentId(commentDto.getRootCommentId())
                .toCommentId(commentDto.getToCommentId())
                .commentLikeCount(0L)
                .updateAt(currentTime)
                .createAt(currentTime)
                .build();
        save(commentDo);
//        审核
        DetectionTaskDto textDetectionTaskDto = DetectionTaskDto.builder()
                .id(uuid)
                .content(commentDto.getContent())
                .url(CALLBACK_URL_COMMENT)
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto);

    }

    @Override
    public void status(CommentStatusDto commentStatusDto) {
        CommentDo commentDo = CommentDo.builder()
                .postCommentId(commentStatusDto.getId())
                .hasShow(commentStatusDto.getStatus())
                .violationInformation(commentStatusDto.getViolationInformation())
                .build();
        updateById(commentDo);
    }
}