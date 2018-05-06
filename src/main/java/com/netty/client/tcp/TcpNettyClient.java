package com.netty.client.tcp;

import com.alibaba.fastjson.JSONObject;
import com.netty.bean.ClientRequest;
import com.netty.bean.DefaultFuture;
import com.netty.bean.Response;
import com.netty.bean.User;
import com.netty.initialzer.ClientInitalizer;
import com.netty.initialzer.tcp.TcpClientInitalizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Created by jack on 2018/5/5.
 */
public class TcpNettyClient  {
    static EventLoopGroup group =null;
    static Bootstrap client =null;
    public static ChannelFuture future=null;
    static {
        group = new NioEventLoopGroup();
        client = new Bootstrap();
        client.group(group);
        client.channel(NioSocketChannel.class);
        client.option(ChannelOption.SO_KEEPALIVE,true);
        client.handler(new TcpClientInitalizer());
        try {
            future = client.connect("localhost", 8080).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Object send(ClientRequest request){
        try{
            System.out.println("客户端向服务端发送请求数据:"+JSONObject.toJSONString(request));
            future.channel().writeAndFlush(JSONObject.toJSONString(request));
            future.channel().writeAndFlush("\r\n");
            DefaultFuture defaultFuture = new DefaultFuture(request);
            Response response = defaultFuture.get(10);
            return response;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        for(int i=0;i<100;i++){
//            ClientRequest request = new ClientRequest();
//            request.setCommand("saveUser");
//            User user = new User();
//            user.setAge(new Random().nextInt(4)*i);
//            user.setId(i);
//            user.setName("jiahp"+i);
//            request.setContent(user);
//            Object result = TcpNettyClient.send(request);
//            System.out.println("客户端长连接测试返回结果:"+JSONObject.toJSONString(result));
//            System.out.println("        ");
//
            new Thread(new UserRequestThread(i)).start();//模拟多线程并发请求
        }
    }

    /**
     * 模拟用户并发请求
     */
    static  class UserRequestThread implements Runnable{
        private int requestId;
        public UserRequestThread(int requestId){
            this.requestId = requestId;
        }

        public void run() {
           synchronized (UserRequestThread.class){
               ClientRequest request = new ClientRequest();
               request.setCommand("saveUser");
               User user = new User();
               user.setAge(new Random().nextInt(4)*requestId);
               user.setId(requestId);
               user.setName("jiahp"+requestId);
               request.setContent(user);
               Object result = TcpNettyClient.send(request);
               System.out.println("客户端长连接测试返回结果:"+JSONObject.toJSONString(result));
               System.out.println("        ");
           }
        }
    }

}


