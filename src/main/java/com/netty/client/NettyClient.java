package com.netty.client;

import com.netty.initialzer.ClientInitalizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * Created by jack on 2018/5/5.
 */
public class NettyClient {
    static EventLoopGroup group =null;
    static Bootstrap client =null;

    static {
        group = new NioEventLoopGroup();
        client = new Bootstrap();
        client.group(group);
        client.channel(NioSocketChannel.class);
        client.option(ChannelOption.SO_KEEPALIVE,true);
        client.handler(new ClientInitalizer());
    }

    /**
     * 监听8080端口
     * @param port
     */
    public static Object connect(int port){
        try {
            ChannelFuture future = client.connect(new InetSocketAddress(port)).sync();
            //客户端准备写入数据到服务端
            future.channel().writeAndFlush("jack");
            future.channel().closeFuture().sync();
            //从handler获取服务端返回来的数据
            Object result = future.channel().attr(AttributeKey.valueOf("username")).get();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }

    public static void main(String[] args) {
        Object result = connect(8080);
        if (result ==null){
            System.out.println("客户端没有获取到返回的数据");
            return;
        }
        System.out.println("客户端获取到的数据是:"+result);
    }

}

