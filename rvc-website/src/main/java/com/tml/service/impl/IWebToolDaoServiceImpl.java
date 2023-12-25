package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.WebToolMapper;
import com.tml.domain.WebToolDO;
import com.tml.service.IWebToolDaoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IWebToolDaoServiceImpl extends ServiceImpl<WebToolMapper, WebToolDO> implements IWebToolDaoService {

    @Override
    public List<WebToolDO> getToolList() {
        return query().list();
    }

}
