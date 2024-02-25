package com.tml.core.gateway.channel;

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

    public static boolean addChannel(String roomId, ChannelHandlerContext channel, String uid){
        return roomInfoMap.containsKey(roomId)&&
                roomChannelMap.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .putIfAbsent(uid, channel.channel()) == null;
    }

    public static Map<String,Channel> findRoomChannel(String roomId){
        return roomChannelMap.get(roomId);
    }
}
