package com.tml.service;

import com.tml.pojo.vo.UserInfoVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2024/2/28
 * @Author xiaochun
 */
@Component
public interface TopService {
    List<UserInfoVO> userTop();

//    List<> moudel();
}
