package com.tml.service;

import com.tml.pojo.form.FeedbackCommentForm;
import com.tml.pojo.form.FeedbackForm;
import com.tml.pojo.vo.FeedbackCommentVO;
import io.github.common.PageVO;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeRegisterException;

public interface FeedbackCommentService {

    Result<PageVO<FeedbackCommentVO>> getCommentList(String uid,int page,int limit,String orders);

    Result<?> addComment(FeedbackCommentForm form,String uid) throws SnowflakeRegisterException;
}
