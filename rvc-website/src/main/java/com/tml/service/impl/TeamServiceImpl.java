package com.tml.service.impl;

import com.tml.service.ITeamDaoService;
import com.tml.service.TeamService;
import io.github.common.web.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    ITeamDaoService teamDaoService;

    @Override
    public Result getTeamList() {
        return Result.success(teamDaoService.getTeamList());
    }
}
