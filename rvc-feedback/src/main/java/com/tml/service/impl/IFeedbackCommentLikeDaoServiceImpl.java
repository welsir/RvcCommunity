package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.exception.RvcSQLException;
import com.tml.mapper.FeedbackCommentLikeMapper;
import com.tml.pojo.FeedbackCommentLike;
import com.tml.service.IFeedbackCommentLikeDaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IFeedbackCommentLikeDaoServiceImpl extends ServiceImpl<FeedbackCommentLikeMapper, FeedbackCommentLike> implements IFeedbackCommentLikeDaoService {

    @Resource
    FeedbackCommentLikeMapper mapper;

    @Override
    public HashSet<Long> getCommentLikeList(String uid, List<Long> cmId) {
        List<FeedbackCommentLike> list = query().select("cm_id")
                .in("cm_id", cmId)
                .eq("uid", uid)
                .list();
        return list.stream().map(FeedbackCommentLike::getCmId).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public FeedbackCommentLike getCommentLike(String uid, Long cmId) {
        return query().select("cm_id")
                .eq("cm_id", cmId)
                .eq("uid", uid)
                .one();
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public boolean addCommentLike(FeedbackCommentLike feedbackCommentLike) throws RvcSQLException {
        try {
            return mapper.insert(feedbackCommentLike)==1;
        }catch (Exception e){
            throw new RvcSQLException("添加评论点赞失败");
        }
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public boolean deleteCommentLike(String uid,Long cmId) throws RvcSQLException {
        try {
            return mapper.deleteByMap(Map.of("uid",uid,"cm_id",cmId))==1;
        }catch (Exception e){
            throw new RvcSQLException("取消评论点赞失败");
        }
    }
}
