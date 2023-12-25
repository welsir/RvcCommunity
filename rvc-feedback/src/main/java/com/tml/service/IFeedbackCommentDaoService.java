package com.tml.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.exception.RvcSQLException;
import com.tml.domain.FeedbackCommentDO;
import com.tml.domain.vo.FeedbackCommentVO;

public interface IFeedbackCommentDaoService {

    Boolean addComment(FeedbackCommentDO commentDO) throws RvcSQLException;

    IPage<FeedbackCommentVO> getCommentList(Long fb_id,int page, int limit, String order);

    Boolean hasComment(Long replyFbId,Long replyCmId);

    Boolean changeCommentLike(Long commentId,Boolean likeStatus);
}
