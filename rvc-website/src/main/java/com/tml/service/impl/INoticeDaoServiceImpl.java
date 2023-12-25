package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.tml.config.QueryParamGroup;
import com.tml.mapper.NoticeMapper;
import com.tml.domain.NoticeDO;
import com.tml.domain.VO.NoticeVO;
import com.tml.service.INoticeDaoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class INoticeDaoServiceImpl extends ServiceImpl<NoticeMapper, NoticeDO> implements INoticeDaoService {

    @Resource
    NoticeMapper mapper;

    @Resource
    QueryParamGroup queryParamGroup;

    @Override
    public IPage<NoticeVO> getNoticeList(int page, int limit, String... params) {
        MPJQueryWrapper<NoticeVO> queryWrapper = new MPJQueryWrapper<NoticeVO>()
                .select(params)
                .orderByAsc("id")
                .orderByAsc("create_at");
        return mapper.selectJoinPage(new Page<>(page, limit),NoticeVO.class,queryWrapper);
    }

    @Override
    public IPage<NoticeVO> getNoticeList(String type, int page, int limit) {
        List<String> queryParams = queryParamGroup.getQueryParams(type);
        return getNoticeList(page,limit,queryParams.toArray(new String[0]));
    }

    @Override
    public NoticeDO getNotice(String noticeId) {
        List<String> queryParams = queryParamGroup.getQueryParams("detail_notice");
        return query().select(queryParams.toArray(new String[0])).eq("notice_id",noticeId).one();
    }

    @Override
    public Boolean watchNotice(String noticeId) {
        return update().setSql("watch_num = watch_num+1").eq("notice_id",noticeId).update();
    }
}
