package com.tml.service.impl;

import com.tml.pojo.FeedbackDO;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.FeedbackService;
import com.tml.service.IFeedbackDaoService;
import io.github.common.PageVO;
import io.github.common.web.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    IFeedbackDaoService feedbackDaoService;

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
    public Result<?> addFeedback(FeedbackForm form, String uid) {
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
