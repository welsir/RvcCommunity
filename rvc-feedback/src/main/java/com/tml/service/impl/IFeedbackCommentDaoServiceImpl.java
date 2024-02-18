package com.tml.service.impl;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.client.NacosPropertySourceBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.constant.QueryType;
import com.tml.constant.dbTableConfig;
import com.tml.exception.RvcSQLException;
import com.tml.mapper.FeedbackCommentMapper;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.service.IFeedbackCommentDaoService;
import io.github.common.JoinSection;
import io.github.query.QueryParamGroup;
import io.github.service.AssistantMJPServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IFeedbackCommentDaoServiceImpl extends AssistantMJPServiceImpl<FeedbackCommentMapper, FeedbackCommentDO> implements IFeedbackCommentDaoService {

    @Resource
    QueryParamGroup queryParamGroup;

    @Value("${rvc.feedback.comment_order_column}")
    List<String> orderColumns;

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Boolean addComment(FeedbackCommentDO commentDO) throws RvcSQLException {
        try {
            return mapper.insert(commentDO)==1;
        }catch (Exception e){
            throw new RvcSQLException("添加评论失败");
        }

    }

    @Override
    public IPage<FeedbackCommentVO> getCommentList(Long fb_id,int page, int limit, String order) {
        List<String> orders = !orderColumns.contains(order) ? List.of("cm_id") : List.of(order,"cm_id");

        JoinSection section = JoinSection.builder()
                .selectSQL("r.uid as reply_uid,r.comment as reply_comment")
                .table(dbTableConfig.RVC_FEEDBACK_COMMENT)
                .asName("r")
                .type(JoinSection.JoinType.LEFT)
                .connectColumn("t.reply_cm_id", "r.cm_id")
                .build();

        return this.BeanPageVOList(
                page,limit,
                queryParamGroup.getQueryParams(QueryType.FEEDBACK_COMMENT,"t"),
                new LinkedHashMap<>(Map.of("t.reply_fb_id",fb_id,"t.has_show",1)),
                List.of(section),
                orders, FeedbackCommentVO.class,false
        );
    }

    @Override
    public Boolean hasComment(Long replyFbId,Long replyCmId) {
        return query().select("cm_id")
                .eq("cm_id",replyCmId)
                .eq("reply_fb_id",replyFbId)
                .count()>0;
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Boolean changeCommentLike(Long commentId, Boolean likeStatus) {
        String sql = likeStatus?"like_num=like_num+1":"like_num=like_num-1";
        return update().setSql(sql).eq("cm_id",commentId).update();
    }
}
