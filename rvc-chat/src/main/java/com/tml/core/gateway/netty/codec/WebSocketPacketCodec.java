package com.tml.core.gateway.netty.codec;

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
//public class WebSocketPacketCodec extends MessageToMessageCodec<WebSocketFrame, Package> {
//
//    public static final WebSocketPacketCodec INSTANCE = new WebSocketPacketCodec();
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
//        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
//        PacketCodeC.INSTANCE.encode(byteBuf, msg);
//        out.add(new BinaryWebSocketFrame(byteBuf));
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
//        out.add(PacketCodeC.INSTANCE.decode(msg.content()));
//    }
//}
