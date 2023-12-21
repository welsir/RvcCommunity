package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.client.UserServiceClient;
import com.tml.common.DetectionStatusEnum;
import com.tml.constant.QueryType;
import com.tml.exception.RvcSQLException;
import com.tml.pojo.FeedbackCommentLike;
import com.tml.pojo.FeedbackDO;
import com.tml.pojo.FeedbackLike;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackService;
import com.tml.service.FeedbackTypeService;
import com.tml.service.IFeedbackDaoService;
import com.tml.service.IFeedbackLikeDaoService;
import io.github.common.PageVO;
import io.github.common.logger.CommonLogger;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import io.github.query.QueryParamGroup;
import io.github.util.PageUtil;
import io.github.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    IFeedbackDaoService feedbackDaoService;

    @Resource
    UserServiceClient userServiceClient;

    @Resource
    IFeedbackLikeDaoService likeDaoService;

    @Resource
    FeedbackTypeService typeService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Resource
    CommonLogger commonLogger;

    @Override
    public Result<PageVO<FeedbackVO>> getFeedbackPageVO(Integer page, Integer limit, String uid, String order) {
        IPage<FeedbackVO> feedbackVOIPage = feedbackDaoService.feedbackPageVO(page, limit, order);
        PageVO<FeedbackVO> pageVO = PageUtil.toPageVO(feedbackVOIPage);

        List<Long> fbIdList = pageVO.getPageList().stream()
                .map(FeedbackVO::getFbid)
                .collect(Collectors.toList());

        //TODO 待优化
        if(StringUtils.hasText(uid)){
            HashSet<Long> likeSet = likeDaoService.getLikeList(uid, fbIdList);

            pageVO.getPageList().forEach(
                    cm -> cm.setHasUp(likeSet.contains(cm.getFbid())?1:0)
            );
        }
        return Result.success(pageVO);
    }

    @Override
    public Result<?> getFeedbackVO(String uid, Long fb_id) {
        FeedbackVO feedbackVO = feedbackDaoService.feedbackVO(fb_id);

        if(feedbackVO!=null){
            if(StringUtils.hasText(uid)){
                FeedbackLike like = likeDaoService.getLike(uid, fb_id);
                feedbackVO.setHasUp(like!=null?1:0);
            }
        }
        return Result.success(Map.of("feedback", feedbackVO));
    }

    @Override
    public List<FeedbackDO> batchFeedbackList(List<String> params, Map<String, List<Object>> inCondition) {
        return null;
    }

    @Override
    public Result<?> addFeedback(FeedbackForm form, String uid) throws SnowflakeRegisterException {
        Integer type = form.getType();
        if (Optional.ofNullable(typeService.hasType(type)).isEmpty()) {
            commonLogger.info("%s 提交的feedback类型不存在",uid);
            return Result.error("403","不存在的feedback类型");
        }
        //TODO 走审核服务流程

        //TODO 测试中
        Long fbid = snowflakeGenerator.generate();
        LocalDateTime today = LocalDateTime.now();
        FeedbackDO feedbackDO = FeedbackDO.builder()
                .uid(uid)
                .createAt(today)
                .updateAt(today)
                .upNum(0L)
                .commentNum(0L)
                .status(0)
                .hasShow(DetectionStatusEnum.DETECTION_SUCCESS.getStatus())
                .build();
        BeanUtils.copyProperties(form,feedbackDO);
        feedbackDO.setFbid(fbid);

        if (feedbackDaoService.feedbackAdd(feedbackDO)) {
            return Result.success(feedbackDO);
        }
        return Result.error("403","添加失败");
    }

    @Override
    public Result<?> updateFeedback(FeedbackForm form, String uid) {
        Long fbid = form.getFbid();

        FeedbackDO feedbackDO = FeedbackDO.builder()
                .updateAt(LocalDateTime.now())
                .content(form.getContent())
                .title(form.getTitle())
                .hasShow(DetectionStatusEnum.DETECTION_SUCCESS.getStatus())
                .build();

        //TODO 进行审核

        if (feedbackDaoService.feedbackUpdate(uid,fbid,feedbackDO)) {
            return Result.success(Map.of("success",true));
        }
        return Result.success(Map.of("success",false));
    }

    @Override
    public Result<?> changeStatus(String uid, String fb_id, Integer status) {
        return null;
    }

    @Override
    public Result<?> deleteFeedback(String uid, Long fb_id) {
        Boolean res = feedbackDaoService.feedbackDelete(uid, fb_id);
        return Result.success(Map.of("success",res));
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Result<?> likeFeedback(String uid, Long fb_id, Boolean isLike) throws SnowflakeRegisterException, RvcSQLException {
        if (isLike) {
            FeedbackLike feedbackLike = FeedbackLike.builder()
                    .id(snowflakeGenerator.generate())
                    .uid(uid)
                    .fbId(fb_id)
                    .createAt(LocalDateTime.now())
                    .build();
            commonLogger.info("用户%s点赞反馈帖子%s",uid,fb_id);
            if (!likeDaoService.addLike(feedbackLike)) {
                return Result.error("403","你已经点过赞了");
            }
        }else{
            commonLogger.info("用户%s取消点赞反馈帖子%s",uid,fb_id);
            if (!likeDaoService.deleteLike(uid,fb_id)) {
                return Result.error("403","你还没有点过赞");
            }
        }

        if (feedbackDaoService.changeLikeNum(fb_id,isLike)) {
            return Result.success(Map.of("success",true));
        }
        return Result.error("403","操作点赞失败");
    }
}
