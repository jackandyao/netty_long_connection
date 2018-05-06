package com.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * Created by jack on 2018/5/5.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端成功连接到服务端就可以通过该方法写入数据到服务端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端成功连接到服务端:"+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端channelRead:接收到服务端的数据是:"+msg.toString());
        //把读取到的数据返回到客户端
        ctx.channel().attr(AttributeKey.valueOf("username")).set(msg.toString());
        //关闭这个channel 客户端那边才能获取到数据
        //这种方式实现的机制就是短连接
        ctx.channel().close();
    }
}
