package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.WebInfoMapper;
import com.tml.pojo.WebInfoDO;
import com.tml.service.IWebInfoDaoService;
import org.springframework.stereotype.Service;

@Service
public class IWebInfoDaoServiceImpl extends ServiceImpl<WebInfoMapper, WebInfoDO> implements IWebInfoDaoService {

    @Override
    public WebInfoDO getWebInfo() {
        return query().one();
    }
}
