package com.tml.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tml.mapper.TeamMapper;
import com.tml.domain.TeamDO;
import com.tml.service.ITeamDaoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ITeamDaoServiceImpl extends ServiceImpl<TeamMapper, TeamDO> implements ITeamDaoService {


    @Override
    public List<TeamDO> getTeamList() {
        return query().list();
    }
}
