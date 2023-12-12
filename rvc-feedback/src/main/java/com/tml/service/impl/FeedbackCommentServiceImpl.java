package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.tml.exception.RvcSQLException;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.form.FeedbackCommentForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.service.FeedbackCommentService;
import com.tml.service.IFeedbackDaoService;
import io.github.common.PageVO;
import io.github.common.logger.CommonLogger;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import io.github.util.PageUtil;
import io.github.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class FeedbackCommentServiceImpl implements FeedbackCommentService {

    @Resource
    IFeedbackCommentDaoServiceImpl feedbackCommentDaoService;

    @Resource
    IFeedbackDaoService feedbackDaoService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Resource
    CommonLogger logger;

    @Override
    public Result<PageVO<FeedbackCommentVO>> getCommentList(Long fb_id,String uid, int page, int limit, String orders) {
        IPage<FeedbackCommentVO> commentList = feedbackCommentDaoService.getCommentList(fb_id,page, limit, orders);
        return Result.success(PageUtil.toPageVO(commentList));
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Result<?> addComment(FeedbackCommentForm form, String uid) throws SnowflakeRegisterException, RvcSQLException {
        Long replyCmId = form.getReplyCmId();
        Long replyFbId = form.getReplyFbId();

        //TODO 待优化 查询优化以及代码优化
        if (!feedbackDaoService.hasFeedback(replyFbId)) {
            return Result.error("403","不存在的帖子");
        }

        if(replyCmId!=null){
            if (!feedbackCommentDaoService.hasComment(replyFbId,replyCmId)) {
                return Result.error("403","不存在的回复评论");
            }
        }

        LocalDateTime today = LocalDateTime.now();
        FeedbackCommentDO feedbackCommentDO = FeedbackCommentDO.builder()
                .hasShow(1)
                .likeNum(0L)
                .createAt(today)
                .updateAt(today)
                .cmid(snowflakeGenerator.generate())
                .replyCmId(replyCmId)
                .replyFbId(replyFbId)
                .comment(form.getComment())
                .uid(uid)
                .build();
        //TODO 审核评论

        //TODO 待优化
        logger.info("%s 添加评论",uid);
        if (feedbackCommentDaoService.addComment(feedbackCommentDO)) {
            logger.info("增加帖子%s评论数",replyFbId);
            feedbackDaoService.feedbackCommentAdd(replyFbId);
            return Result.success(feedbackCommentDO);
        }
        return Result.error("403","评论失败");
    }
}
