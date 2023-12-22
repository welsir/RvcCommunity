package com.tml.service;

import com.tml.exception.RvcSQLException;
import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.pojo.dto.UpdatePasswordDTO;
import com.tml.pojo.dto.UserInfoDTO;
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

    void update(UserInfoDTO userInfoDTO, String uid, String username);

    void follow(String followUid, String uid, String username) throws RvcSQLException;

    void updatePassword(UpdatePasswordDTO updatePasswordDTO, String uid, String username);

    UserInfoVO getUserInfo(String uid, String username);

    void avatar(MultipartFile file, String uid, String username);

    boolean exist(String uid);
}
