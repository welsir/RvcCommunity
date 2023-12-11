package com.tml.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.pojo.FeedbackDO;
import com.tml.pojo.vo.FeedbackVO;

public interface IFeedbackDaoService {

    IPage<FeedbackVO> feedbackPageVO(int page,int limit,String order);

    FeedbackVO feedbackVO(Long fb_id);
    Boolean feedbackAdd(FeedbackDO feedbackDO);

    Boolean feedbackUpdate(String uid,Long fb_id,FeedbackDO feedback);
    Boolean feedbackDelete(String uid,Long fb_id);

    Boolean feedbackCommentAdd(String fb_id);
}
