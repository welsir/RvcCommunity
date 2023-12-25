package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.domain.form.FeedbackCommentForm;
import com.tml.domain.vo.FeedbackCommentVO;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeRegisterException;

public interface FeedbackCommentService {

    Result<PageVO<FeedbackCommentVO>> getCommentList(Long fb_id,String uid,int page,int limit,String orders);

    Result<?> addComment(FeedbackCommentForm form,String uid) throws SnowflakeRegisterException, RvcSQLException;

    Result<?> likeComment(Long comment_id,String uid,Boolean likeStatus) throws RvcSQLException, SnowflakeRegisterException;
}
