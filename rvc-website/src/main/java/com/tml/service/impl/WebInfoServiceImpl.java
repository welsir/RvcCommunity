package com.tml.service.impl;

import com.tml.service.IWebInfoDaoService;
import com.tml.service.WebInfoService;
import io.github.common.web.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WebInfoServiceImpl implements WebInfoService {

    @Resource
    private IWebInfoDaoService webInfoDaoService;

    @Override
    public Result getWebInfo() {
        return Result.success(webInfoDaoService.getWebInfo());
    }

}
