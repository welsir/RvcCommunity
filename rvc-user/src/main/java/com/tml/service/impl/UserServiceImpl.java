package com.tml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.client.FileServiceClient;
import com.tml.common.rabbitmq.RabbitMQListener;
import com.tml.config.FileConfig;
import com.tml.exception.ServerException;
import com.tml.mapper.UserDataMapper;
import com.tml.mapper.UserFollowMapper;
import com.tml.mapper.UserInfoMapper;
import com.tml.domain.DO.*;
import com.tml.domain.DTO.ReceiveUploadFileDTO;
import com.tml.domain.Result;
import com.tml.domain.VO.UploadModelForm;
import com.tml.domain.dto.*;
import com.tml.domain.enums.ResultEnums;
import com.tml.domain.vo.UserInfoVO;
import com.tml.service.UserService;
import com.tml.util.*;
import org.springframework.beans.BeanUtils;
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

import static com.tml.config.DetectionConfig.*;
import static com.tml.domain.enums.EmailEnums.*;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    FileServiceClient fileServiceClient;

    @Resource
    UserFollowMapper userFollowMapper;

    @Resource
    UserDataMapper userDataMapper;

    @Resource
    RabbitMQListener rabbitMQListener;

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
    public void logout(String uid, String username) {
        TokenUtil.logout(uid, username);
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
        userInfo.setUsername(RandomStringUtil.generateRandomString());
        userInfoMapper.insert(userInfo);
        userData.setUid(userInfo.getUid());
        userDataMapper.insert(userData);
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
    public void update(UserInfoDTO userInfoDTO, String uid, String username) {
        UserInfo user = userInfoMapper.selectByUid(uid);
        boolean flag = false;

        if(!userInfoDTO.getNickname().equals(user.getNickname())){
            DetectionTaskDTO nicknameTask = DetectionTaskDTO.builder()
                    .id(uid)
                    .name(USER_NICKNAME)
                    .content(userInfoDTO.getNickname())
                    .build();
            rabbitMQListener.submit(nicknameTask, "text");
            user.setNickname("审核中");
            flag = true;
        }

        if(!userInfoDTO.getDescription().equals(user.getDescription())){
            DetectionTaskDTO descriptionTask = DetectionTaskDTO.builder()
                    .id(uid)
                    .name(USER_DESCRIPTION)
                    .content(userInfoDTO.getDescription())
                    .build();
            rabbitMQListener.submit(descriptionTask, "text");
            user.setDescription("审核中");
            flag = true;
        }
        switch (userInfoDTO.getSex()){
            case "男":
            case "女":
                if(user.getSex() == null || !user.getSex().equals(userInfoDTO.getSex())){
                    user.setSex(userInfoDTO.getSex());
                    flag = true;
                }
                break;
            default: throw new ServerException(ResultEnums.SEX_VALUE_ERROR);
        }

        if(user.getBirthday() == null || user.getBirthday() == userInfoDTO.getBirthday()){
            user.setBirthday(userInfoDTO.getBirthday());
            flag = true;
        }

        if(flag){
            user.setUpdatedAt(LocalDateTime.now());
            userInfoMapper.updateById(user);
        }
    }

    @Override
    public void follow(String followUid, String uid, String username) {
        if(Objects.equals(followUid, uid)){
            throw new ServerException(ResultEnums.CANT_FOLLOW_YOURSELF);
        }
        if(!userInfoMapper.exist("uid", followUid)){
            throw new ServerException(ResultEnums.USER_NOT_EXIST);
        }
        QueryWrapper<UserFollow> followWrapper = new QueryWrapper<>();
        followWrapper
                .eq("follow_uid", uid)
                .eq("followed_uid", followUid);
        if(userFollowMapper.selectCount(followWrapper) > 0){
            userFollowMapper.delete(followWrapper);
            return;
        }
        UserFollow follow = new UserFollow();
        follow.setFollowUid(uid);
        follow.setFollowedUid(followUid);
        try {
            userFollowMapper.insert(follow);
        } catch (Exception e){
            throw new ServerException(ResultEnums.FAIL_FOLLOW);
        }
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO, String uid, String username) {
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
    public UserInfoVO getUserInfo(String uid, String username) {
        UserInfo userInfo = userInfoMapper.selectByUid(uid);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, userInfoVO);
        UserData userData = userDataMapper.selectByUid(uid);
        BeanUtils.copyProperties(userData, userInfoVO);
        return userInfoVO;
    }

    @Override
    public void avatar(MultipartFile file, String uid, String username){
        try {
            UploadModelForm form = UploadModelForm.builder()
                    .bucket(FileConfig.USER_BUCKET)
                    .path(FileConfig.USER_PATH)
                    .md5(org.springframework.util.DigestUtils.md5DigestAsHex(file.getInputStream()))
                    .file(file)
                    .build();
            Result<ReceiveUploadFileDTO> result = fileServiceClient.uploadModel(form);
            if(result.getCode() != 200){
                throw new ServerException(result.getCode().toString(), result.getMessage());
            }
            ReceiveUploadFileDTO receiveUploadFileDTO = result.getData();
            DetectionTaskDTO imageTask = DetectionTaskDTO.builder()
                    .id(uid)
                    .name(USER_AVATAR)
                    .content(receiveUploadFileDTO.getUrl())
                    .build();
            rabbitMQListener.submit(imageTask, "image");
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
