package com.tml.service.impl;

import com.tml.service.WebToolService;
import io.github.common.web.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WebToolServiceImpl implements WebToolService {

    @Resource
    IWebToolDaoServiceImpl webToolDaoService;

    @Override
    public Result getToolList() {
        return Result.success(webToolDaoService.getToolList());
    }
}
