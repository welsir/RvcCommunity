package com.tml.core.gateway.handler;

import com.alibaba.fastjson.JSON;
import com.tml.common.AccessMessage;
import com.tml.core.gateway.channel.handler.ChannelHandler;
import com.tml.core.gateway.utils.AccessMessageUtils;
import io.netty.channel.Channel;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/5 0:38
 */
public class CenterServerHandler implements ChannelHandler{

    @Override
    public void receive(Channel channel, Object message) {
        //中央消息分发器
        AccessMessage msg = (AccessMessage) message;
        Handler handler = CenterRouter.router(msg.getCmd());
        Object result;
        try {
            result = handler.handle(channel,msg);
        }catch (RuntimeException e){
            result = e.getMessage();
        }
        if (result == null) {
            return;
        }
        channel.writeAndFlush(AccessMessageUtils.createResponse(msg.getCmd(), JSON.toJSONBytes(result)));
    }

    @Override
    public void send(Channel channel, Object message) {

    }

    @Override
    public void connect(Channel channel) {

    }
}
