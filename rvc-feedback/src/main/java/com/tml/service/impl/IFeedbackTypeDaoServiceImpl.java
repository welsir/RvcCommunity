package com.tml.service.impl;

import com.tml.mapper.FeedbackTypeMapper;
import com.tml.domain.FeedbackTypeDO;
import com.tml.service.IFeedbackTypeDaoService;
import io.github.service.AssistantServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class IFeedbackTypeDaoServiceImpl extends AssistantServiceImpl<FeedbackTypeMapper, FeedbackTypeDO> implements IFeedbackTypeDaoService {
}
