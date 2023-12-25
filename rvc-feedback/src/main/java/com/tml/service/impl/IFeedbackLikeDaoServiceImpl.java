package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.exception.RvcSQLException;
import com.tml.mapper.FeedbackLikeMapper;
import com.tml.pojo.FeedbackLike;
import com.tml.service.IFeedbackLikeDaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IFeedbackLikeDaoServiceImpl extends ServiceImpl<FeedbackLikeMapper, FeedbackLike> implements IFeedbackLikeDaoService {

    @Resource
    FeedbackLikeMapper mapper;

    @Override
    public HashSet<Long> getLikeList(String uid, List<Long> fbId) {
        List<FeedbackLike> list = query().select("fb_id")
                .in("fb_id", fbId)
                .eq("uid", uid)
                .list();
        return list.stream().map(FeedbackLike::getFbId).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public FeedbackLike getLike(String uid, Long fbId) {
        return query().select("fb_id")
                .eq("fb_id", fbId)
                .eq("uid", uid)
                .one();
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public boolean addLike(FeedbackLike feedbackLike) throws RvcSQLException {
        try {
            return mapper.insert(feedbackLike)==1;
        }catch (Exception e){
            throw new RvcSQLException("添加反馈点赞失败");
        }
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public boolean deleteLike(String uid, Long fbId) throws RvcSQLException {
        try {
            return mapper.deleteByMap(Map.of("uid",uid,"fb_id",fbId))==1;
        }catch (Exception e){
            throw new RvcSQLException("取消反馈点赞失败");
        }
    }
}
