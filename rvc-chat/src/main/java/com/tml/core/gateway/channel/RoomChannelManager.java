package com.tml.core.gateway.channel;

import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.ChatRoom;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/24 15:27
 */
public class RoomChannelManager {

    private static final Map<String, ChatRoom> roomInfoMap = new ConcurrentHashMap<>();

    private static final Map<String, Map<String, Channel>> roomChannelMap= new ConcurrentHashMap<>();
    public static void createGroup(String roomId,ChatRoom chatRoom){
        roomInfoMap.put(roomId,chatRoom);
    }

    public static boolean addChannel(String roomId, Channel channel, UserInfoVO user){
        boolean b = roomInfoMap.containsKey(roomId);
        if(b){
            roomInfoMap.get(roomId).getUserIdList().add(user);
            roomChannelMap.put(roomId,Map.of(user.getUid(),channel));
            return true;
        }
        return false;
    }

    public static Map<String,Channel> findRoomChannel(String roomId){
        return roomChannelMap.get(roomId);
    }

    public static boolean tryAdd(String roomId,String pwd,Channel channel,UserInfoVO user){
        if(roomInfoMap.get(roomId).getPassword().equals(pwd)){
            return addChannel(roomId,channel,user);
        }else{
            return false;
        }
    }


}
