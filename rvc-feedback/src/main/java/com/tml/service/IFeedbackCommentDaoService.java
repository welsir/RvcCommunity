package com.tml.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.pojo.vo.FeedbackVO;

public interface IFeedbackCommentDaoService {

    Boolean addComment(FeedbackCommentDO commentDO);

    IPage<FeedbackCommentVO> getCommentList(int page, int limit, String order);
}
