package com.tml.controller;

import com.google.gson.JsonObject;
import com.tml.annotation.apiAuth.WhiteApi;
import com.tml.pojo.Result;
import com.tml.pojo.dto.ChatRoom;
import com.tml.pojo.vo.CreateRoomRequest;
import com.tml.pojo.vo.EnterRequest;
import com.tml.pojo.vo.RoomDetailInfoVO;
import com.tml.service.RoomService;
import io.github.id.snowflake.SnowflakeGenerator;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 15:53
 */
@RequestMapping("room")
public class ChatController {

    @Resource
    private RoomService roomService;

    /**
     * @description: 创建房间
     * @param: chatRoomRequest
     * @return: Result<RoomDetailInfoVO>
     **/
    @WhiteApi
    @RequestMapping("create")
    public void createRoom(
            @RequestBody CreateRoomRequest request,
                @RequestHeader(value = "uid") String uid){
            roomService.createRoom(request,uid);
    }

    @RequestMapping("entry")
    @WhiteApi
    public Result<String> entryRoom(@RequestBody EnterRequest enterRequest){
        return Result.success(roomService.joinRoom(enterRequest));
    }
}
