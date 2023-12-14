package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.common.UserContext;
import com.tml.exception.GlobalExceptionHandler;
import com.tml.exception.ServerException;
import com.tml.mapper.UserFollowMapper;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.DO.AuthUser;
import com.tml.pojo.DO.UserFollow;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.pojo.dto.UserInfoDTO;
import com.tml.pojo.enums.ResultEnums;
import com.tml.pojo.vo.UserInfoVO;
import com.tml.service.UserService;
import com.tml.util.CopyUtil;
import com.tml.util.CodeUtil;
import com.tml.util.RandomStringUtil;
import com.tml.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tml.pojo.enums.EmailEnums.*;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${user.retry.max-retries}")
    int MAX_RETRIES;

    @Value("${user.retry.retry-interval}")
    int RETRY_INTERVAL;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    UserFollowMapper userFollowMapper;

    @Resource
    CodeUtil codeUtil;

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

        if(!codeUtil.emailVerify(email, REGISTER, emailCode)) {
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
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                String errorMessage = e.getMessage();
                if(errorMessage.contains("username_UNIQUE")){
                    logger.error("生成用户名相同，正在重试...");
                    if (retryCount < MAX_RETRIES - 1) {
                        try {
                            Thread.sleep(RETRY_INTERVAL);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if(errorMessage.contains("email_UNIQUE")){
                    throw new ServerException(ResultEnums.EMAIL_EXIST);
                } else {
                    throw new ServerException("500", "数据库插入错误");
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
    public Map<String, String> preCode() {
        return codeUtil.image();
    }

    @Override
    public void sendCode(String email,String code, String uuid, int type) {
        if(!codeUtil.preVerify(uuid, code)){
            throw new ServerException(ResultEnums.PRE_CODE_ERROR);
        }
        switch (type){
            case 0:
                codeUtil.sendCode(email, REGISTER);
                break;
            case 1:
                codeUtil.sendCode(email, LOGIN);
                break;
            case 2:
                codeUtil.sendCode(email, PASSWORD);
                break;
            default:
                throw new ServerException(ResultEnums.FAIL_SEND_VER_CODE);
        }
    }

    @Override
    public UserInfoVO one(String uid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        UserInfoVO userInfoVO = new UserInfoVO();
        UserInfo user = userInfoMapper.selectOne(queryWrapper);
        if(user == null){
            throw new ServerException(ResultEnums.UID_NOT_EIXST);
        }
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    public List<UserInfoVO> list(List<String> uidList) {
        if(uidList.isEmpty()){
            throw new ServerException(ResultEnums.UID_LIST_IS_EMPTY);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uidList);
        List<UserInfoVO> userList = new ArrayList<>();
        List<UserInfo> userInfoList = userInfoMapper.selectList(queryWrapper);
        if(userInfoList.isEmpty()){
            throw new ServerException(ResultEnums.NO_ONE_EXIST);
        }
        CopyUtil.copyPropertiesForList(userInfoList, userList, UserInfoVO.class);
        return userList;
    }

    @Override
    public void update(UserInfoDTO userInfoDTO) {

    }

    @Override
    public void follow(String uid) {
        AuthUser authUser = UserContext.getCurrentUser();
        QueryWrapper<UserInfo> userQuery = new QueryWrapper<>();
        userQuery.eq("uid", uid);
        if(userInfoMapper.selectCount(userQuery) <= 0){
            throw new ServerException(ResultEnums.USER_NOT_EXIST);
        }
        UserFollow follow = new UserFollow();
        follow.setFollowUid(authUser.getUid());
        follow.setFollowedUid(uid);
        try {
            userFollowMapper.insert(follow);
        } catch (Exception e){
            throw new ServerException(ResultEnums.FAIL_FOLLOW);
        }
    }

    private String loginByPsw(String email, String password){
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("email", email);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if(userInfo == null || !userInfo.getPassword().equals(password)){
            throw new ServerException(ResultEnums.WRONG_USERNAME_OR_PASSWORD);
        }
        return TokenUtil.getToken(userInfo.getUid(), userInfo.getUsername());
    }

    private String loginByCode(String email, String code){
        if (!codeUtil.emailVerify(email, LOGIN,code)) {
            throw new ServerException(ResultEnums.VER_CODE_ERROR);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("email", email);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        return TokenUtil.getToken(userInfo.getUid(), userInfo.getUsername());
    }
}
