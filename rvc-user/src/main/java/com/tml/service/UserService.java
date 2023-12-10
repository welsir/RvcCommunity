package com.tml.service;

import com.tml.pojo.dto.LoginDTO;
import com.tml.pojo.dto.RegisterDTO;
import org.springframework.stereotype.Component;

/**
 * @Date 2023/12/10
 * @Author xiaochun
 */
@Component
public interface UserService {
    String login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);

    boolean sendCode(String email);
}
