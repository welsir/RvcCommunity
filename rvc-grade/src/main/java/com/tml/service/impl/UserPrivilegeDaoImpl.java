package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.domain.entity.RvcLevelPrivilege;
import com.tml.domain.entity.RvcLevelRole;
import com.tml.domain.entity.RvcLevelRolePrivilege;
import com.tml.domain.entity.RvcLevelUser;
import com.tml.mapper.RvcLevelPrivilegeMapper;
import com.tml.mapper.RvcLevelRoleMapper;
import com.tml.mapper.RvcLevelRolePrivilegeMapper;
import com.tml.mapper.RvcLevelUserMapper;
import com.tml.service.UserPrivilegeDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @NAME: UserPrivilegeDaoImpl
 * @USER: yuech
 * @Description:
 * @DATE: 2024/2/27
 */
@Service
public class UserPrivilegeDaoImpl implements UserPrivilegeDao {

    @Resource
    private RvcLevelRoleMapper rvcLevelRoleMapper;

    @Resource
    private RvcLevelPrivilegeMapper rvcLevelPrivilegeMapper;

    @Resource
    private RvcLevelUserMapper rvcLevelUserMapper;

    @Resource
    private RvcLevelRolePrivilegeMapper rvcLevelRolePrivilegeMapper;

    @Override
    public List<String> getPrivilege(String uid) {
        //获取用户经验值
        RvcLevelUser rvcLevelUser = rvcLevelUserMapper.selectById(uid);
        //获取用户角色信息
        QueryWrapper<RvcLevelRole> rvcLevelRoleQueryWrapper = new QueryWrapper<RvcLevelRole>()
                .ge("min_exp",rvcLevelUser.getExp())
                .le("max_exp",rvcLevelUser.getExp());
        RvcLevelRole rvcLevelRole = rvcLevelRoleMapper.selectOne(rvcLevelRoleQueryWrapper);
        //获取角色对应权限id
        QueryWrapper<RvcLevelRolePrivilege> roleEq = new QueryWrapper<RvcLevelRolePrivilege>()
                .eq("role_id", rvcLevelRole.getId());
        List<RvcLevelRolePrivilege> rvcLevelRolePrivileges = rvcLevelRolePrivilegeMapper.selectList(roleEq);
        List<String> privilegeIds = rvcLevelRolePrivileges.stream()
                .map(RvcLevelRolePrivilege::getPrivilegeId)
                .collect(Collectors.toList());
        //返回用户权限信息
        QueryWrapper<RvcLevelPrivilege> privilegeQueryWrapper = new QueryWrapper<RvcLevelPrivilege>()
                .in("id", privilegeIds);
        List<RvcLevelPrivilege> privileges = rvcLevelPrivilegeMapper.selectList(privilegeQueryWrapper);
        return privileges.stream()
                .map(RvcLevelPrivilege::getUrl)
                .collect(Collectors.toList());
    }
}