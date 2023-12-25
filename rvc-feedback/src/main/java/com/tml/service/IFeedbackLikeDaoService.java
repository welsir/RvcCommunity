package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.pojo.FeedbackLike;

import java.util.HashSet;
import java.util.List;

public interface IFeedbackLikeDaoService {
    HashSet<Long> getLikeList(String uid, List<Long> fbId);

    FeedbackLike getLike(String uid, Long fbId);

    boolean addLike(FeedbackLike feedbackLike) throws RvcSQLException;

    boolean deleteLike(String uid,Long fbId) throws RvcSQLException;

}
