package com.netty.initialzer;

import com.netty.handler.ClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by jack on 2018/5/5.
 */
public class ClientInitalizer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new ClientHandler());
    }
}
