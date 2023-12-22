package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.tml.client.UserServiceClient;
import com.tml.exception.RvcSQLException;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.FeedbackCommentLike;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.form.FeedbackCommentForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackCommentService;
import com.tml.service.IFeedbackCommentLikeDaoService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackCommentServiceImpl implements FeedbackCommentService {

    @Resource
    IFeedbackCommentDaoServiceImpl feedbackCommentDaoService;

    @Resource
    IFeedbackCommentLikeDaoService commentLikeDaoService;

    @Resource
    IFeedbackDaoService feedbackDaoService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Resource
    UserServiceClient userServiceClient;

    @Resource
    CommonLogger logger;

    @Override
    public Result<PageVO<FeedbackCommentVO>> getCommentList(Long fb_id,String uid, int page, int limit, String orders) {
        IPage<FeedbackCommentVO> commentList = feedbackCommentDaoService.getCommentList(fb_id,page, limit, orders);
        PageVO<FeedbackCommentVO> pageVO = PageUtil.toPageVO(commentList);


        List<String> uidList = pageVO.getPageList().stream()
                .map(FeedbackCommentVO::getUid)
                .collect(Collectors.toList());

        List<String> replyUidList = pageVO.getPageList().stream()
                .map(FeedbackCommentVO::getReplyUid)
                .filter(replyUid -> StringUtils.hasText(replyUid))
                .collect(Collectors.toList());

        //TODO 待聚合
        Map<String, UserInfoVO> map = userServiceClient.list(uidList).getData();

        Map<String, UserInfoVO> replyUidMap = userServiceClient.list(replyUidList).getData();

        if(map!=null){
            pageVO.getPageList().forEach(
                    feedbackCommentVO -> {
                        String search_uid = feedbackCommentVO.getUid();
                        UserInfoVO userInfoVO = map.get(search_uid);
                        if(userInfoVO!=null){
                            feedbackCommentVO.setAvatar(userInfoVO.getAvatar());
                            feedbackCommentVO.setNickname(userInfoVO.getNickname());
                            feedbackCommentVO.setUsername(userInfoVO.getUsername());
                        }
                    }
            );
        }

        if(replyUidMap!=null){
            pageVO.getPageList().forEach(
                    feedbackCommentVO -> {
                        String search_reply_uid = feedbackCommentVO.getReplyUid();
                        UserInfoVO replyUserInfoVO = replyUidMap.get(search_reply_uid);
                        if(replyUserInfoVO!=null){
                            feedbackCommentVO.setReplyAvatar(replyUserInfoVO.getAvatar());
                            feedbackCommentVO.setReplyNickname(replyUserInfoVO.getNickname());
                            feedbackCommentVO.setReplyUsername(replyUserInfoVO.getUsername());
                        }
                    }
            );
        }
        //TODO 待优化
        if(StringUtils.hasText(uid)){

            List<Long> cmIdList = pageVO.getPageList().stream()
                    .map(FeedbackCommentVO::getCmid)
                    .collect(Collectors.toList());

            HashSet<Long> likeSet = commentLikeDaoService.getCommentLikeList(uid, cmIdList);

            pageVO.getPageList().forEach(
                    cm -> cm.setHasLike(likeSet.contains(cm.getCmid())?1:0)
            );
        }
        return Result.success(pageVO);
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

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Result<?> likeComment(Long comment_id, String uid, Boolean likeStatus) throws RvcSQLException, SnowflakeRegisterException {

        if (likeStatus) {
            FeedbackCommentLike commentLike = FeedbackCommentLike.builder()
                    .id(snowflakeGenerator.generate())
                    .uid(uid)
                    .cmId(comment_id)
                    .createAt(LocalDateTime.now())
                    .build();
            logger.info("用户%s点赞评论%s",uid,comment_id);
            if (!commentLikeDaoService.addCommentLike(commentLike)) {
                return Result.error("403","你已经点过赞了1");
            }
        }else{
            logger.info("用户%s取消点赞评论%s",uid,comment_id);
            if (!commentLikeDaoService.deleteCommentLike(uid,comment_id)) {
                return Result.error("403","你还没有点过赞");
            }
        }

        if (feedbackCommentDaoService.changeCommentLike(comment_id,likeStatus)) {
            return Result.success(Map.of("success",true));
        }
        return Result.error("403","操作点赞失败");
    }
}
