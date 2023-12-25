package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.domain.FeedbackDO;
import com.tml.domain.form.FeedbackForm;
import com.tml.domain.vo.FeedbackVO;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeRegisterException;

import java.util.List;
import java.util.Map;

public interface FeedbackService {

    Result<PageVO<FeedbackVO>> getFeedbackPageVO(Integer page, Integer limit, String uid, String order);

    Result<?> getFeedbackVO(String uid,Long fb_id);

    List<FeedbackDO> batchFeedbackList(List<String> params, Map<String,List<Object>> inCondition);

    Result<?> addFeedback(FeedbackForm form,String uid) throws SnowflakeRegisterException;

    Result<?> updateFeedback(FeedbackForm form,String uid);

    Result<?> changeStatus(String uid,String fb_id,Integer status);

    Result<?> deleteFeedback(String uid,Long fb_id);

    Result<?> likeFeedback(String uid,Long fb_id,Boolean isLike) throws SnowflakeRegisterException, RvcSQLException;
}
