package com.tml.core.gateway.channel.handler;

import com.alibaba.fastjson.JSONObject;
import com.tml.client.UserServiceClient;
import com.tml.common.CommandEnum;
import com.tml.core.gateway.channel.RoomChannelManager;
import com.tml.core.store.redis.RedisStoreRepoImpl;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.dto.ChatRoom;
import io.grpc.netty.shaded.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:06
 */
@Service
public class ChatRoomHandler extends AbstractHandler{

    @Resource
    RedisStoreRepoImpl redisStoreRepo;
    @Resource
    UserServiceClient userServiceClient;

    @Override
    protected Object doHandle(ChannelHandlerContext channel, JSONObject message) {

        String id = message.get("roomId").toString();

        ChatRoom chatRoom = redisStoreRepo.queryChatroomInfo(id);
        if(chatRoom==null){
            //抛异常:房间不存在
            throw new RuntimeException();
        }
        int cmd = (int) message.get("command");

        if(CommandEnum.CHATROOM_JOIN.getCommand() == cmd){
            return handleJoin(channel,message,id);
        }else if(CommandEnum.CHATROOM_LEAVE.getCommand() == cmd){
            return handleLeave(channel,message,id);
        }else if(CommandEnum.CHATROOM_SEND.getCommand() == cmd){
            return handleSendMsg(channel,message,id);
        }else if(CommandEnum.CHATROOM_CREATE.getCommand() == cmd){
            return handleCreateGroup(channel,message,id);
        }
        else{
            //抛异常
            throw new RuntimeException();
        }
    }

    private Object handleJoin(ChannelHandlerContext channel,JSONObject msg,String roomId){
        Map<String, Channel> roomChannel = RoomChannelManager.findRoomChannel(roomId);
        if(null!=roomChannel){
            String uid = msg.getString("uid");
            RoomChannelManager.addChannel(roomId,channel,uid);
            UserInfoVO user = userServiceClient.one(uid).getData();
            for (String key : roomChannel.keySet()) {
                roomChannel.get(key).writeAndFlush(new TextWebSocketFrame(user.getNickname()+"加入房间"));
            }
            return true;
        }else {
            //房间不存在!
            throw new RuntimeException();
        }
    }

    private Object handleLeave(ChannelHandlerContext channel,JSONObject msg,String roomId){
        return null;
    }
    private Object handleSendMsg(ChannelHandlerContext channel,JSONObject msg,String roomId){return null;}

    //监听用户channel将channel加入channelMap
    private Object handleCreateGroup(ChannelHandlerContext channel,JSONObject msg,String roomId){
        String uid = msg.getString("uid");
        boolean res = RoomChannelManager.addChannel(roomId, channel, uid);
        if(res){
            channel.channel().write(new TextWebSocketFrame("成功创建聊天室!"));
            return true;
        }else{
            channel.channel().write(new TextWebSocketFrame("聊天室创建失败"));
            return false;
        }
    }
}
