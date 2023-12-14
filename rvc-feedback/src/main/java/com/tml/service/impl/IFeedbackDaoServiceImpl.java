package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.constant.QueryType;
import com.tml.constant.dbTableConfig;
import com.tml.exception.RvcSQLException;
import com.tml.mapper.FeedbackMapper;
import com.tml.pojo.FeedbackDO;
import com.tml.pojo.vo.FeedbackVO;
import com.tml.service.DetectionService;
import com.tml.service.IFeedbackDaoService;
import io.github.common.JoinSection;
import io.github.query.QueryParamGroup;
import io.github.service.AssistantMJPServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IFeedbackDaoServiceImpl extends AssistantMJPServiceImpl<FeedbackMapper, FeedbackDO> implements IFeedbackDaoService, DetectionService {

    @Resource
    QueryParamGroup queryParamGroup;

    @Value("${rvc.feedback.feedback_order_column}")
    List<String> orderColumns;

    @Override
    public IPage<FeedbackVO> feedbackPageVO(int page, int limit, String order) {
        List<String> orders = !orderColumns.contains(order) ? List.of("fb_id") : List.of(order,"fb_id");

        JoinSection typeJoin = JoinSection.builder()
                .type(JoinSection.JoinType.LEFT)
                .table(dbTableConfig.RVC_FEEDBACK_TYPE)
                .asName("type")
                .connectColumn("t.type = type.id")
                .selectSQL("type.type as typeName")
                .build();

        JoinSection statusJoin = JoinSection.builder()
                .type(JoinSection.JoinType.LEFT)
                .table(dbTableConfig.RVC_FEEDBACK_STATUS)
                .asName("status")
                .connectColumn("t.status = status.id")
                .selectSQL("status.status as statusName")
                .build();

        return this.BeanPageVOList(page,limit,
                queryParamGroup.getQueryParams(QueryType.FEEDBACK_LIST,"t"),
                new LinkedHashMap<>(Map.of("has_show",1)),
                List.of(typeJoin,statusJoin),
                orders, FeedbackVO.class,true);
    }

    @Override
    public FeedbackVO feedbackVO(Long fb_id) {

        JoinSection typeJoin = JoinSection.builder()
                .type(JoinSection.JoinType.LEFT)
                .table(dbTableConfig.RVC_FEEDBACK_TYPE)
                .asName("type")
                .connectColumn("t.type = type.id")
                .selectSQL("type.type as typeName")
                .build();

        JoinSection statusJoin = JoinSection.builder()
                .type(JoinSection.JoinType.LEFT)
                .table(dbTableConfig.RVC_FEEDBACK_STATUS)
                .asName("status")
                .connectColumn("t.status = status.id")
                .selectSQL("status.status as statusName")
                .build();

        FeedbackVO feedbackVO = this.getBeanVO(
                queryParamGroup.getQueryParams(QueryType.FEEDBACK_LIST,"t"),
                new LinkedHashMap<>(Map.of("fb_id", fb_id,"has_show",1)),
                List.of(typeJoin,statusJoin),
                FeedbackVO.class
        );
        return feedbackVO;
    }

    @Override
    public Boolean feedbackAdd(FeedbackDO feedbackDO) {
        return baseMapper.insert(feedbackDO)>0;
    }

    @Override
    public Boolean feedbackUpdate(String uid, Long fb_id, FeedbackDO feedback) {
        return this.updateBean(feedback,Map.of("fb_id",fb_id,"uid",uid));
    }

    @Override
    public Boolean feedbackDelete(String uid, Long fb_id) {
        return this.deleteBean(Map.of("fb_id",fb_id,"uid",uid));
    }

    @Override
    @Transactional(rollbackFor = RvcSQLException.class)
    public Boolean feedbackCommentAdd(Long fb_id) throws RvcSQLException {
        if(!update().setSql("comment_num = comment_num+1").eq("fb_id",fb_id).update()){
            throw new RvcSQLException("更新评论数失败");
        }
        return true;
    }

    @Override
    public Boolean hasFeedback(Long fb_id) {
        return query().select("fb_id").eq("fb_id",fb_id).count()>0;
    }

    @Override
    public Boolean changeDetection(Long fb_id, Integer status) {
        FeedbackDO build = FeedbackDO.builder()
                .hasShow(status)
                .build();
        return this.updateBean(build,Map.of("fb_id",fb_id));
    }
}
