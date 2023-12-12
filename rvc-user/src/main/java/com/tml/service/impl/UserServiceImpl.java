package com.tml.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.exception.ServerException;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.pojo.enums.ResultEnums;
import com.tml.pojo.vo.UserInfoVO;
import com.tml.service.UserService;
import com.tml.util.CopyUtil;
import com.tml.util.EmailUtil;
import com.tml.util.RandomStringUtil;
import com.tml.util.TokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Service
public class UserServiceImpl implements UserService {
    @Value("${user.retry.max-retries}")
    int MAX_RETRIES;

    @Value("${user.retry.retry-interval}")
    int RETRY_INTERVAL;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    EmailUtil emailUtil;

    @Override
    public Map<String, String> login(LoginDTO loginDTO) {
        String emailCode = loginDTO.getEmailCode();
        String password = loginDTO.getPassword();
        if(emailCode != null && password != null){
            throw new ServerException(ResultEnums.LOGIN_FORM_ERROR);
        }
        String email = loginDTO.getEmail();
        String token;
        if(emailCode == null){
            token = loginByPsw(email, password);
        } else{
            token = loginByCode(email, emailCode);
        }
        if (token == null) {
            throw new ServerException(ResultEnums.FAIL_LOGIN);
        }
        return Map.of("token", token);
    }

    @Override
    public Map<String, String> register(RegisterDTO registerDTO) {
        String email = registerDTO.getEmail();
        String emailCode = registerDTO.getEmailCode();
        String password = registerDTO.getPassword();

        if(!emailUtil.verify(email, "Register", emailCode)) {
            throw new ServerException(ResultEnums.VER_CODE_ERROR);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setRegisterData(LocalDateTime.now());
        userInfo.setUpdatedAt(LocalDateTime.now());
        int retryCount = 0;
        boolean success = false;
        while (retryCount < MAX_RETRIES && !success) {
            try {
                userInfo.setUsername(RandomStringUtil.generateRandomString());
                userInfoMapper.insert(userInfo);
                success = true;
            } catch (DuplicateKeyException e) {
                e.printStackTrace();
                if (retryCount < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            retryCount++;
        }
        if(!success){
            throw new ServerException(ResultEnums.FAIL_REGISTER);
        }
        return Map.of("token", TokenUtil.getToken(userInfo.getUid(), userInfo.getUsername()));
    }

    @Override
    public void sendCode(String email, boolean type) {
        try {
            if (type) {
                emailUtil.sendCode(email, "Login");
            } else {
                emailUtil.sendCode(email, "Register");
            }
        } catch (Exception e){
            throw new ServerException(ResultEnums.FAIL_SEND_VER_CODE);
        }
    }

    @Override
    public UserInfoVO one(String uid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfoMapper.selectOne(queryWrapper), userInfoVO);
        return userInfoVO;
    }

    @Override
    public List<UserInfoVO> list(List<String> uidList) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uidList);
        List<UserInfoVO> userList = new ArrayList<>();
        CopyUtil.copyPropertiesForList(userInfoMapper.selectList(queryWrapper), userList, UserInfoVO.class);
        return userList;
    }

    private String loginByPsw(String email, String password){
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("email", email);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if(!userInfo.getPassword().equals(password)){
            throw new ServerException(ResultEnums.WRONG_USERNAME_OR_PASSWORD);
        }

        return StpUtil.getTokenValueByLoginId(userInfo.getUid() + "|" + userInfo.getUsername());
    }

    private String loginByCode(String email, String code){
        if (!emailUtil.verify(email, "Login",code)) {
            throw new ServerException(ResultEnums.VER_CODE_ERROR);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("email", email);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        return TokenUtil.getToken(userInfo.getUid(), userInfo.getUsername());
    }
}
