package com.tml.service;

import com.tml.pojo.FeedbackTypeDO;

import java.util.List;

public interface FeedbackTypeService {

    FeedbackTypeDO hasType(Integer id);
    List<FeedbackTypeDO> queryAll();
}
