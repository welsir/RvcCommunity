package com.tml.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public String login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String emailCode = loginDTO.getEmailCode();
        String password = loginDTO.getPassword();
        if(Objects.equals(emailCode, "")){

        }

        return null;
    }

    @Override
    public String register(RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public boolean sendCode(String email) {
        return true;
    }


}
