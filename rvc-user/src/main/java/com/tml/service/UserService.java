package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.pojo.dto.*;
import com.tml.pojo.vo.UserInfoVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Component
public interface UserService {
    Map<String, String> login(LoginDTO loginDTO);

    void logout(String uid, String username);

    Map<String, String> register(RegisterDTO registerDTO) throws RvcSQLException;

    Map<String, String> preCode();

    void sendCode(String email, String code, String uuid, int type);

    UserInfoVO one(String uid);

    Map<String, UserInfoVO> list(List<String> uidList);

    void update(UserInfoDTO userInfoDTO, String uid);

    void follow(String followUid, String uid) throws RvcSQLException;

    void updatePassword(UpdatePasswordDTO updatePasswordDTO, String uid);

    void forgotPassword(ForgotPassword forgotPassword);

    UserInfoVO getUserInfo(String uid);

    Map<String, ?> getUserInfoById(String targetUid, String uid);

    void avatar(MultipartFile file, String uid);

    boolean exist(String uid);

    List<UserInfoVO> getMyFollowUser(String uid);

    boolean isFollowed(String uid1, String uid2);
}
