package com.tml.core.gateway.handler;

import com.tml.client.UserServiceClient;
import com.tml.common.AccessExceptionResponse;
import com.tml.common.AccessMessage;

import com.tml.common.CommandEnum;
import com.tml.core.gateway.channel.RoomChannelManager;
import com.tml.core.gateway.protocol.PacketCodec;
import com.tml.core.gateway.protocol.request.CreateGroupRequest;
import com.tml.core.gateway.protocol.response.CreateGroupResponse;
import com.tml.core.store.redis.RedisStoreRepoImpl;
import com.tml.pojo.VO.UserInfoVO;
import com.tml.pojo.vo.JoinGroupRequest;
import io.grpc.netty.shaded.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/23 13:06
 */
@Service
public class ChatRoomHandler extends AbstractHandler {

    @Resource
    RedisStoreRepoImpl redisStoreRepo;
    @Autowired
    UserServiceClient userServiceClient;
    @Resource
    PacketCodec packetCodec;

    @Override
    protected Object doHandle(Channel channel, AccessMessage msg) {

        int cmd = msg.getCmd();

        if(CommandEnum.CHATROOM_JOIN.getCommand() == cmd){
            return handleJoin(channel,msg);
        }else if(CommandEnum.CHATROOM_LEAVE.getCommand() == cmd){
            return handleLeave(channel,msg);
        }else if(CommandEnum.CHATROOM_SEND.getCommand() == cmd){
            return handleSendMsg(channel,msg);
        }else if(CommandEnum.CHATROOM_CREATE.getCommand() == cmd){
            return handleCreateGroup(channel,msg);
        }else{
            return null;
        }
    }

    private Object handleJoin(Channel channel,AccessMessage msg){
        byte cmd = msg.getCmd();
        Byte algorithm = msg.getSerializerAlgorithm();
        JoinGroupRequest packet = (JoinGroupRequest) packetCodec.decodeRequest(cmd, algorithm, msg.getBody());
        String roomId = packet.getRoomId();
        String uid = packet.getUid();
        String password = packet.getPassword();
        Map<String, Channel> roomChannel = RoomChannelManager.findRoomChannel(roomId);
        if(null!=roomChannel){
            //保存用户信息
            UserInfoVO user = userServiceClient.one(uid).getData();
            RoomChannelManager.tryAdd(roomId,password,channel,user);
            for (String key : roomChannel.keySet()) {
                roomChannel.get(key).writeAndFlush(new TextWebSocketFrame(user.getNickname()+"加入房间"));
            }
            return true;
        }else {
            //房间不存在!
            return AccessExceptionResponse.ROOM_NOT_EXIT;
        }
    }

    private Object handleLeave(Channel channel,AccessMessage msg){
        return null;
    }
    private Object handleSendMsg(Channel channel,AccessMessage msg){return null;}

    //监听用户channel将channel加入channelMap
    private Object handleCreateGroup(Channel channel, AccessMessage msg){
        CreateGroupRequest request = (CreateGroupRequest) packetCodec.decodeRequest(msg.getCmd(),msg.getSerializerAlgorithm(),msg.getBody());
        if(request==null){
            //抛异常
            return null;
        }
        String uid = request.getUid();
        String roomId = request.getRoomId();
        UserInfoVO user = userServiceClient.one(uid).getData();
        boolean res = RoomChannelManager.addChannel(roomId, channel, user);
        CreateGroupResponse response = new CreateGroupResponse();
        if(res){
            response.setSuccess(true);
            response.setRoomId(roomId);
            return response;
        }else{
            response.setRoomId(roomId);
            response.setException(AccessExceptionResponse.ROOM_NOT_EXIT);
            response.setSuccess(false);
            return response;
        }
    }
}
