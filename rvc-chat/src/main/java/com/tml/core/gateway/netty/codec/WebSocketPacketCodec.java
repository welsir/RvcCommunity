package com.tml.core.gateway.netty.codec;

import com.tml.common.AccessMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/26 13:21
 */
public class WebSocketPacketCodec extends MessageToMessageCodec<WebSocketFrame, AccessMessage> {

    public static final WebSocketPacketCodec INSTANCE = new WebSocketPacketCodec();

    @Override
    protected void encode(ChannelHandlerContext ctx, AccessMessage msg, List<Object> out) {
        ByteBuf byteBuf = null;
        try {
            byteBuf= ctx.channel().alloc().ioBuffer();
            MessageCodec.encode(byteBuf,msg);
            BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
            out.add(frame);
            byteBuf = null;
        }finally {
            if(byteBuf!=null){
                byteBuf.release();
            }
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) {
        if(msg instanceof BinaryWebSocketFrame){
            ByteBuf buf = msg.content();
            if(buf ==null){
                return;
            }
            out.add(MessageCodec.decode(msg.content()));
        }
    }
}
