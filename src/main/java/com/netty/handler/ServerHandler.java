package com.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by jack on 2018/5/5.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端接收来自客户端的请求连接:"+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端接收到客户端的数据是:"+msg.toString());
        ctx.writeAndFlush("wecome to word,"+msg.toString());
    }
}
