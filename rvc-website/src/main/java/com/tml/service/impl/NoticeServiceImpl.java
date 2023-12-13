package com.tml.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.protobuf.ServiceException;
import com.tml.common.handler.AbstractStrategyFactory;
import com.tml.config.PageLimitConfiguration;
import com.tml.pojo.NoticeDO;
import com.tml.pojo.VO.NoticeVO;
import com.tml.service.INoticeDaoService;
import com.tml.service.NoticeService;
import com.tml.utils.PageUtil;
import io.github.common.web.PageVO;
import io.github.common.web.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.tml.constant.HandlerName.LOGIN_WATCH_NOTICE;
import static com.tml.constant.HandlerName.NO_LOGIN_WATCH_NOTICE;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    PageLimitConfiguration pageLimitConfiguration;

    @Resource
    INoticeDaoService noticeDaoService;

    @Resource
    AbstractStrategyFactory strategyFactory;

    /**
     * 获取首页公告栏轮播图
     * @return Result<PageVO<NoticeVO>>
     */
    @Override
    public Result<PageVO<NoticeVO>> getHomeNoticeList() {
        Integer homeNoticeLimit = pageLimitConfiguration.getHomeNoticeLimit();
        IPage<NoticeVO> noticeDOPage = noticeDaoService.getNoticeList("home_notice", 1, homeNoticeLimit);
        PageVO<NoticeVO> noticePageVO = new PageUtil<NoticeVO>().toPageVO(noticeDOPage);
        return Result.success(noticePageVO);
    }

    /**
     * 获取公告页面的公告列表
     * @return Result<PageVO<NoticeVO>>
     */
    @Override
    public Result<PageVO<NoticeVO>> getWebNoticeList(Integer page) {
        Integer homeNoticeLimit = pageLimitConfiguration.getHomeNoticeLimit();
        IPage<NoticeVO> noticeDOPage = noticeDaoService.getNoticeList("home_notice", page, homeNoticeLimit);
        PageVO<NoticeVO> noticePageVO = new PageUtil<NoticeVO>().toPageVO(noticeDOPage);
        return Result.success(noticePageVO);
    }

    public Result<NoticeVO> getWebNoticeDetail(String noticeId,String uid){
        NoticeDO notice = noticeDaoService.getNotice(noticeId);
        //TODO 用户是否点赞
        NoticeVO noticeVO = new NoticeVO();
        BeanUtils.copyProperties(notice,noticeVO);
        return Result.success(noticeVO);
    }

    @Override
    public Result watchNotice(String noticeId, String uid) throws ServiceException {
        String type = StringUtils.hasText(uid)?LOGIN_WATCH_NOTICE:NO_LOGIN_WATCH_NOTICE;
        uid = StringUtils.hasText(uid)?uid:"";
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        return strategyFactory.handlerRes(type,Map.of("noticeId",noticeId,"uid",uid));
    }

}
