package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.constant.QueryType;
import com.tml.mapper.FeedbackCommentMapper;
import com.tml.pojo.FeedbackCommentDO;
import com.tml.pojo.vo.FeedbackCommentVO;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.IFeedbackCommentDaoService;
import io.github.query.QueryParamGroup;
import io.github.service.AssistantMJPServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class IFeedbackCommentDaoServiceImpl extends AssistantMJPServiceImpl<FeedbackCommentMapper, FeedbackCommentDO> implements IFeedbackCommentDaoService {

    @Resource
    QueryParamGroup queryParamGroup;

    @Value("${rvc.feedback.feedback_order_column}")
    List<String> orderColumns;

    @Override
    public Boolean addComment(FeedbackCommentDO commentDO) {
        return mapper.insert(commentDO)==1;
    }

    @Override
    public IPage<FeedbackCommentVO> getCommentList(int page, int limit, String order) {

        List<String> orders = !orderColumns.contains(order) ? List.of("fb_id") : List.of("fb_id",order);

        return this.BeanPageVOList(
                page,limit,
                queryParamGroup.getQueryParams(QueryType.FEEDBACK_COMMENT),
                Map.of("has_show",1),
                orders, FeedbackCommentVO.class,true
        );
    }
}
