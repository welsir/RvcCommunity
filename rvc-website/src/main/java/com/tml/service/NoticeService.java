package com.tml.service;

import com.google.protobuf.ServiceException;
import com.tml.domain.VO.NoticeVO;
import io.github.common.web.PageVO;
import io.github.common.web.Result;

public interface NoticeService {

    Result<PageVO<NoticeVO>> getHomeNoticeList();

    Result<PageVO<NoticeVO>> getWebNoticeList(Integer page);

    Result<NoticeVO> getWebNoticeDetail(String noticeId,String uid);

    Result watchNotice(String noticeId,String uid) throws ServiceException;
}
