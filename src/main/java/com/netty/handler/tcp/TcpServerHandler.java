package com.netty.handler.tcp;

import com.alibaba.fastjson.JSONObject;
import com.netty.Media;
import com.netty.bean.Response;
import com.netty.bean.ServerRequest;
import com.netty.bean.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.Charset;

/**
 * Created by jack on 2018/5/5.
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof ByteBuf){
            ByteBuf req = (ByteBuf)msg;
            String content = req.toString(Charset.defaultCharset());
            System.out.println("服务端开始读取客户端的请求数据:"+content);

            ServerRequest request = JSONObject.parseObject(content,ServerRequest.class);
            //Object result = Media.execute(request);真实环境中应该是通过反射调用方法动态执行结果
            JSONObject user = (JSONObject) request.getContent();
            user.put("success","ok");

            Response res = new Response();
            res.setId(request.getId());
            res.setContent(user);
            ctx.channel().write(JSONObject.toJSONString(res));
            ctx.channel().writeAndFlush("\r\n");
            System.out.println("      ");
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {

        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.equals(IdleState.READER_IDLE)){
                System.out.println("读空闲====");
                ctx.close();
            }else if(event.equals(IdleState.WRITER_IDLE)){
                System.out.println("写空闲====");
            }else if(event.equals(IdleState.WRITER_IDLE)){
                System.out.println("读写空闲====");
                ctx.channel().writeAndFlush("ping\r\n");
            }

        }

        super.userEventTriggered(ctx, evt);
    }
}
