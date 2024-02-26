package com.tml.core.store.redis;

import com.tml.config.NettyServerConfiguration;
import com.tml.pojo.dto.ChatRoom;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 14:28
 */
@Component
public class RedisStoreRepoImpl {

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private NettyServerConfiguration serverConfiguration;

    private final String KEY_ALL_CHATROOM = "chatroom:all";
    private final String KEY_CHATROOM_NODE = "chatroom:node";
    public void save(ChatRoom room) {
        redisTemplate.opsForHash().put(KEY_ALL_CHATROOM,room.getId(),room);
        redisTemplate.opsForHash().put(KEY_CHATROOM_NODE,room.getId(),serverConfiguration.getNettyServer());
    }

    public ChatRoom queryChatroomInfo(String roomId){
        return (ChatRoom) redisTemplate.opsForHash().get(KEY_ALL_CHATROOM,roomId);
    }
    public String queryChatRoomNode(String roomId){
        return (String) redisTemplate.opsForHash().get(KEY_CHATROOM_NODE,roomId);
    }

    public List<ChatRoom> queryChatroomAll(){
        return null;
    }

}
