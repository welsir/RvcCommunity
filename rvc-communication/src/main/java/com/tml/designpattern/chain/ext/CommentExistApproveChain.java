package com.tml.designpattern.chain.ext;

import com.tml.designpattern.chain.ApproveChain;
import com.tml.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static com.tml.constant.DBConstant.RVC_COMMUNICATION_COMMENT;

/**
 * @NAME: CommentExistApproveChain
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/25
 */
@Component
@RequiredArgsConstructor
public class CommentExistApproveChain extends ApproveChain {

    private final CommentMapper commentMapper;
    @Override
    public boolean approve() {
        if (commentMapper.existsRecord(RVC_COMMUNICATION_COMMENT,"post_comment_id",  getParams())){
            //下一个处理器
            return getNextChain().approve();
        }
        throw new RuntimeException("父评论不存在");
    }
}