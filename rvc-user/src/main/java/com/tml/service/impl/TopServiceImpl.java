package com.tml.service.impl;

import com.tml.config.RedisBaseConfig;
import com.tml.pojo.vo.UserInfoVO;
import com.tml.service.TopService;
import com.tml.util.TopUtil;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Date 2024/2/28
 * @Author xiaochun
 */
@Service
public class TopServiceImpl implements TopService {
    @Resource
    TopUtil topUtil;

    @Override
    public List<UserInfoVO> userTop() {
        //获取分数最高的100位用户的信息
        List<UserInfoVO> list = new ArrayList<>();
        topUtil.top(RedisBaseConfig.TOP_USER_BASE, list, UserInfoVO.class);
        return list;
    }
}
