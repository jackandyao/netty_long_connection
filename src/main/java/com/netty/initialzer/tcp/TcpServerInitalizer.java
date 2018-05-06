package com.netty.initialzer.tcp;

import com.netty.handler.ServerHandler;
import com.netty.handler.tcp.TcpServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by jack on 2018/5/5.
 */
public class TcpServerInitalizer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
        ch.pipeline().addLast(new IdleStateHandler(6, 2, 1, TimeUnit.SECONDS));
        ch.pipeline().addLast(new TcpServerHandler());
        ch.pipeline().addLast(new StringEncoder());
    }
}
