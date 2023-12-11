package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.form.FeedbackCommentForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.service.FeedbackCommentService;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import io.github.util.PageUtil;
import io.github.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class FeedbackCommentServiceImpl implements FeedbackCommentService {

    @Resource
    IFeedbackCommentDaoServiceImpl feedbackCommentDaoService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Override
    public Result<PageVO<FeedbackCommentVO>> getCommentList(String uid, int page, int limit, String orders) {
        IPage<FeedbackCommentVO> commentList = feedbackCommentDaoService.getCommentList(page, limit, orders);
        return Result.success(PageUtil.toPageVO(commentList));
    }

    @Override
    public Result<?> addComment(FeedbackCommentForm form, String uid) throws SnowflakeRegisterException {
        String replyUid = form.getReplyUid();
        if (StringUtils.hasText(replyUid)&&replyUid.equals(uid)) {
            return Result.error("403","无法回复自己");
        }
        LocalDateTime today = LocalDateTime.now();
        FeedbackCommentDO feedbackCommentDO = FeedbackCommentDO.builder()
                .hasShow(1)
                .likeNum(0L)
                .createAt(today)
                .updateAt(today)
                .cmid(snowflakeGenerator.generate())
                .replyUid(replyUid)
                .comment(form.getComment())
                .uid(uid)
                .build();
        //TODO 审核评论

        if (feedbackCommentDaoService.addComment(feedbackCommentDO)) {
            return Result.success(feedbackCommentDO);
        }
        return Result.error("403","评论失败");
    }
}
