package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.domain.FeedbackCommentLike;

import java.util.HashSet;
import java.util.List;

public interface IFeedbackCommentLikeDaoService {

    HashSet<Long> getCommentLikeList(String uid, List<Long> cmId);

    FeedbackCommentLike getCommentLike(String uid,Long cmId);

    boolean addCommentLike(FeedbackCommentLike feedbackCommentLike) throws RvcSQLException;

    boolean deleteCommentLike(String uid,Long cmId) throws RvcSQLException;
}
