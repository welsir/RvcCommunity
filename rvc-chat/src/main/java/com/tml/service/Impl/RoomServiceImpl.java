package com.tml.service.Impl;

import com.google.gson.JsonObject;
import com.tml.client.UserServiceClient;
import com.tml.core.gateway.channel.RoomChannelManager;
import com.tml.core.store.memory.MemoryStoreImpl;
import com.tml.core.store.redis.RedisStoreRepoImpl;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.ChatRoom;
import com.tml.pojo.vo.CreateGroupAuth;

import com.tml.pojo.vo.EnterRequest;
import com.tml.pojo.vo.RoomDetailInfoVO;
import com.tml.service.RoomService;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 17:08
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    MemoryStoreImpl memoryStore;
    @Resource
    RedisStoreRepoImpl redisStore;
    @Resource
    SnowflakeGenerator snowflakeGenerator;
    @Resource
    UserServiceClient userServiceClient;

    @Override
    public RoomDetailInfoVO createRoom(CreateGroupAuth request, String uid) {

        try {
            Result<UserInfoVO> response = userServiceClient.one(uid);
            UserInfoVO userInfo = response.getData();
            Long id = snowflakeGenerator.generate();
            ChatRoom room = ChatRoom.builder()
                    .id(id.toString())
                    .name(request.getTitle() == null ? userInfo.getNickname() + "的房间" : request.getTitle())
                    .userIdList(List.of(userInfo))
                    .build();
            //todo:存入数据到redis 1.房间列表(维护目前所有房间列表) 2.房间信息
            memoryStore.subscribe(uid, room.getId());
            redisStore.save(room);
            RoomChannelManager.createGroup(room.getId(),room);
            RoomDetailInfoVO roomDetailInfoVO = new RoomDetailInfoVO();
            BeanUtils.copyProperties(room,roomDetailInfoVO);
            roomDetailInfoVO.setOwnerId(uid);
            return roomDetailInfoVO;
        } catch (SnowflakeRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String joinRoom(EnterRequest request) {
        return redisStore.queryChatRoomNode(request.getRoomId());
    }
}
