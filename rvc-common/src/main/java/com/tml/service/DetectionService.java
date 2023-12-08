package com.tml.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 服务审核后调用接口
 */
public interface DetectionService {

    Boolean changeDetection(Long id,Integer status);
}
