package com.tml.pojo.dto;

import com.tml.pojo.DO.UserInfo;
import com.tml.pojo.VO.UserInfoVO;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 15:27
 */
@Data
@Builder
public class ChatRoom {

    private String id;
    private String name;
    private String picture;
    private String password;
    private List<UserInfoVO> userIdList;
}
