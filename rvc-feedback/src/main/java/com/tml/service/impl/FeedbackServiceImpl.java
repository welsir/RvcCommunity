package com.tml.service.impl;

import com.tml.common.DetectionStatusEnum;
import com.tml.pojo.FeedbackDO;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackService;
import com.tml.service.FeedbackTypeService;
import com.tml.service.IFeedbackDaoService;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    IFeedbackDaoService feedbackDaoService;

    @Resource
    FeedbackTypeService typeService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Override
    public Result<PageVO<FeedbackVO>> getFeedbackPageVO(Integer page, Integer limit, String uid, String order) {
        return null;
    }

    @Override
    public Result<?> getFeedbackVO(String uid, String fb_id) {
        return null;
    }

    @Override
    public List<FeedbackDO> batchFeedbackList(List<String> params, Map<String, List<Object>> inCondition) {
        return null;
    }

    @Override
    public Result<?> addFeedback(FeedbackForm form, String uid) throws SnowflakeRegisterException {
        Integer type = form.getType();
        if (Optional.ofNullable(typeService.hasType(type)).isEmpty()) {
            return Result.error("403","不存在的feedback类型");
        }
        //TODO 走审核服务流程
        Long fbid = snowflakeGenerator.generate();

        LocalDateTime today = LocalDateTime.now();

        FeedbackDO.builder()
                .fbid(fbid)
                .createAt(today)
                .updateAt(today)
                .hasShow(DetectionStatusEnum.UN_DETECTION.getStatus());
        return null;
    }

    @Override
    public Result<?> updateFeedback(FeedbackForm form, String uid) {
        return null;
    }

    @Override
    public Result<?> changeStatus(String uid, String fb_id, Integer status) {
        return null;
    }

    @Override
    public Result<?> deleteFeedback(String uid, String fb_id) {
        return null;
    }
}
