package com.tml.service.Impl;

import com.google.gson.JsonObject;
import com.tml.client.UserServiceClient;
import com.tml.core.store.memory.MemoryStoreImpl;
import com.tml.core.store.redis.RedisStoreImpl;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.ChatRoom;
import com.tml.pojo.vo.CreateRoomRequest;
import com.tml.pojo.vo.RoomDetailInfoVO;
import com.tml.service.RoomService;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import io.netty.channel.ChannelHandlerContext;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
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
    RedisStoreImpl redisStore;
    @Resource
    SnowflakeGenerator snowflakeGenerator;
    @Resource
    UserServiceClient userServiceClient;

    @Override
    public void createRoom(ChannelHandlerContext ctx, Object params, String uid) {
        try {
            Result<UserInfoVO> response = userServiceClient.one(uid);
            UserInfoVO userInfo = response.getData();
            Long id = snowflakeGenerator.generate();
            if (params instanceof CreateRoomRequest) {
                CreateRoomRequest createRoomRequest = (CreateRoomRequest) params;
                ChatRoom room = ChatRoom.builder()
                        .id(id.toString())
                        .name(createRoomRequest.getTitle() == null ? userInfo.getNickname() + "的房间" : createRoomRequest.getTitle())
                        .userIdList(List.of(uid))
                        .build();
                //todo:存入数据到redis 1.房间列表(维护目前所有房间列表) 2.房间信息
                memoryStore.subscribe(uid, room.getId());
                redisStore.save(room);
            } else {
                //抛异常
            }
        } catch (SnowflakeRegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
