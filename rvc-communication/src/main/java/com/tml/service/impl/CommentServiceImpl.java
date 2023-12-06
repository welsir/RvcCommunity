package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.CommentMapper;
import com.tml.mq.producer.handler.ProducerHandler;
import com.tml.pojo.dto.CommentDto;
import com.tml.pojo.dto.DetectionTaskDto;
import com.tml.pojo.dto.PageInfo;
import com.tml.pojo.entity.Comment;
import com.tml.service.CommentService;
import com.tml.utils.BeanUtils;
import com.tml.utils.Uuid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.tml.constant.DetectionConstants.STATUS_UNDERREVIEW;


/**
 * @NAME: CommentServiceImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/30
 */
@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public void comment(CommentDto commentDto) {
//        保存用户评论信息
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        String uuid = Uuid.getUuid();
        Comment commentDo = Comment.builder()
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
                .name("comment.text")
                .build();

        ProducerHandler producerHandler = BeanUtils.getBean(ProducerHandler.class);
        producerHandler.submit(textDetectionTaskDto,"text");

    }



    @Override
    public Page<Comment> list(PageInfo<String> params) {

        String postId = params.getData();
        Integer pageNum = params.getPage();
        Integer pageSize = params.getLimit();

        Page<Comment> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostCommentId, postId)
                .and(wrapper -> wrapper.eq(Comment::getHasShow, 1)); // hasshow 等于 1 的条件

        Page<Comment> list = this.page(page,queryWrapper);
        return list;
    }
}