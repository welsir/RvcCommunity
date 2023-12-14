package com.tml.service;

import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import com.tml.pojo.dto.UserInfoDTO;
import com.tml.pojo.vo.UserInfoVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Component
public interface UserService {
    Map<String, String> login(LoginDTO loginDTO);

    Map<String, String> register(RegisterDTO registerDTO);

    void sendCode(String email, boolean type);

    UserInfoVO one(String uid);

    List<UserInfoVO> list(List<String> uidList);

    void update(UserInfoDTO userInfoDTO);

    void follow(String uid);
}
