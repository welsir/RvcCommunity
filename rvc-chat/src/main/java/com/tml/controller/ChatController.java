package com.tml.controller;

import com.tml.pojo.Result;
import com.tml.pojo.vo.ChatRoomRequest;
import com.tml.pojo.vo.RoomDetailInfoVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/19 15:53
 */
@RequestMapping("/room")
public class ChatController {

    /**
     * @description: 创建房间
     * @param: chatRoomRequest
     * @return: Result<RoomDetailInfoVO>
     **/
    @PostMapping("/create")
    public Result<RoomDetailInfoVO> createRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        return null;
    }

}
