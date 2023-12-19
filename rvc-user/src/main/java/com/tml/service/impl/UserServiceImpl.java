package com.tml.service.impl;

import com.alibaba.nacos.api.model.v2.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.client.FileServiceClient;
import com.tml.common.rabbitmq.UserRabbitMQListener;
import com.tml.config.DetectionConfig;
import com.tml.exception.GlobalExceptionHandler;
import com.tml.exception.ServerException;
import com.tml.mapper.UserDataMapper;
import com.tml.mapper.UserFollowMapper;
import com.tml.mapper.UserInfoMapper;
import com.tml.pojo.DO.AuthUser;
import com.tml.pojo.DO.UserData;
import com.tml.pojo.DO.UserFollow;
import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.DTO.ReceiveUploadFileDTO;
import com.tml.pojo.VO.UploadModelForm;
import com.tml.pojo.dto.*;
import com.tml.pojo.enums.ResultEnums;
import com.tml.pojo.vo.UserInfoVO;
import com.tml.service.UserService;
import com.tml.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    FileServiceClient fileServiceClient;

    @Resource
    UserFollowMapper userFollowMapper;

    @Resource
    UserDataMapper userDataMapper;

    @Resource
    UserRabbitMQListener userRabbitMQListener;

    @Resource
    CodeUtil codeUtil;

    @Resource
    UserUtil userUtil;

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
    public void logout() {
//        AuthUser authUser = UserContext.getCurrentUser();
        AuthUser authUser = userUtil.getCurrentUser();
        TokenUtil.logout(authUser.getUid(), authUser.getUsername());
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
        UserData userData = new UserData("0",0,0,0,0);
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
                userData.setUid(userInfo.getUid());
                userDataMapper.insert(userData);
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
    public Map<String, UserInfoVO> list(List<String> uidList) {
        if(uidList.isEmpty()){
            throw new ServerException(ResultEnums.UID_LIST_IS_EMPTY);
        }
        QueryWrapper<UserInfo> userQuery = new QueryWrapper<>();
        userQuery.in("uid", uidList);

        List<UserInfoVO> userList = new ArrayList<>();
        CopyUtil.copyPropertiesForList(userInfoMapper.selectList(userQuery), userList, UserInfoVO.class);
        return userList.stream().collect(Collectors.toMap(UserInfoVO::getUid, userInfoVO -> userInfoVO));
    }

    @Override
    public void update(UserInfoDTO userInfoDTO) {
//        AuthUser authUser = UserContext.getCurrentUser();
        AuthUser authUser = userUtil.getCurrentUser();

        DetectionTaskDTO nicknameTask = DetectionTaskDTO.builder()
                                .id(authUser.getUid())
                                .name(DetectionConfig.USER_DESCRIPTION)
                                .content(userInfoDTO.getNickname())
                                .build();

        DetectionTaskDTO descriptionTask = DetectionTaskDTO.builder()
                .id(authUser.getUid())
                .name(DetectionConfig.USER_NICKNAME)
                .content(userInfoDTO.getNickname())
                .build();

        userRabbitMQListener.textSubmit(nicknameTask);
        userRabbitMQListener.textSubmit(descriptionTask);
    }

    @Override
    public void follow(String uid) {
//        AuthUser authUser = UserContext.getCurrentUser();
        AuthUser authUser = userUtil.getCurrentUser();
        if(Objects.equals(uid, authUser.getUid())){
            throw new ServerException(ResultEnums.CANT_FOLLOW_YOURSELF);
        }
        if(userInfoMapper.exist("uid", uid)){
            throw new ServerException(ResultEnums.USER_NOT_EXIST);
        }
        QueryWrapper<UserFollow> followWrapper = new QueryWrapper<>();
        followWrapper
                .eq("follow_uid", authUser.getUid())
                .eq("followed_uid", uid);
        if(userFollowMapper.selectCount(followWrapper) > 0){
            userFollowMapper.delete(followWrapper);
            return;
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

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        if(!codeUtil.emailVerify(updatePasswordDTO.getEmail(), PASSWORD, updatePasswordDTO.getEmailCode())){
            throw new ServerException(ResultEnums.VER_CODE_ERROR);
        }

        QueryWrapper<UserInfo> userWrapper = new QueryWrapper<>();
        userWrapper.eq("email", updatePasswordDTO.getEmail());
        UserInfo userInfo = userInfoMapper.selectOne(userWrapper);
        userInfo.setPassword(updatePasswordDTO.getPassword());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfoVO getUserInfo() {
        AuthUser authUser = userUtil.getCurrentUser();
        QueryWrapper<UserInfo> userWrapper = new QueryWrapper<>();
        userWrapper.eq("uid", authUser.getUid());
        UserInfo userInfo = userInfoMapper.selectOne(userWrapper);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, userInfoVO);
        QueryWrapper<UserData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", authUser.getUid());
        UserData userData = userDataMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(userData, userInfoVO);
        return userInfoVO;
    }

    @Override
    public void avatar(MultipartFile file){
        try {
            AuthUser authUser = userUtil.getCurrentUser();
            UploadModelForm form = UploadModelForm.builder()
                    .bucket("rvc")
                    .path("rvc/user/avatar")
                    .md5(org.springframework.util.DigestUtils.md5DigestAsHex(file.getInputStream()))
                    .build();
            Result<ReceiveUploadFileDTO> result = fileServiceClient.uploadModel(form);
            if(result.getCode() != 200){
                throw new ServerException(result.getCode().toString(), result.getMessage());
            }
            ReceiveUploadFileDTO receiveUploadFileDTO = result.getData();
            DetectionTaskDTO imageTask = DetectionTaskDTO.builder()
                    .id(authUser.getUid())
                    .name(DetectionConfig.USER_AVATAR)
                    .content(receiveUploadFileDTO.getUrl())
                    .build();
            userRabbitMQListener.imageSubmit(imageTask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist(String uid) {
        if(!userInfoMapper.exist("uid", uid)){
            throw new ServerException(ResultEnums.USER_NOT_EXIST);
        }
        return true;
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
        if (!codeUtil.emailVerify(email, LOGIN, code)) {
            throw new ServerException(ResultEnums.VER_CODE_ERROR);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("email", email);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        return TokenUtil.getToken(userInfo.getUid(), userInfo.getUsername());
    }
}
