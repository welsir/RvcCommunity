package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.pojo.dto.*;
import com.tml.pojo.vo.UserInfoVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.github.common.web.Result;

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

    void resetPwdEmailCode(String email, String code, String uuid, String uid);

    UserInfoVO one(String uid);

    Map<String, UserInfoVO> list(List<String> uidList);

    void update(UserInfoDTO userInfoDTO, String uid);

    boolean follow(String followUid, String type, String uid) throws RvcSQLException;

    boolean updatePassword(UpdatePasswordDTO updatePasswordDTO, String uid);

    void forgotPassword(ForgotPassword forgotPassword);

    UserInfoVO getUserInfo(String uid);

    Map<String, ?> getUserInfoById(String targetUid, String uid);

    void avatar(MultipartFile file, String uid);

    boolean exist(String uid);

    List<UserInfoVO> getMyFollowUser(String uid);

    boolean isFollowed(String uid1, String uid2);
}
