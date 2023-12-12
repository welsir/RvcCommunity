package com.tml.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tml.pojo.NoticeDO;
import com.tml.pojo.VO.NoticeVO;

public interface INoticeDaoService {

    IPage<NoticeVO> getNoticeList(int page, int limit, String...params);

    IPage<NoticeVO> getNoticeList(String type,int page, int limit);

    NoticeDO getNotice(String noticeId);
    Boolean watchNotice(String noticeId);
}
