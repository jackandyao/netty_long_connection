package com.netty.handler.tcp;

import com.alibaba.fastjson.JSONObject;
import com.netty.bean.DefaultFuture;
import com.netty.bean.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * Created by jack on 2018/5/5.
 */
public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg.toString().equals("ping")){
            ctx.channel().writeAndFlush("ping\r\n");
            return ;
        }
        System.out.println("客户端获取到服务端响应数据:"+msg.toString());

        String str = getJSONObject(msg.toString()).toString();
        Response res = JSONObject.parseObject(str, Response.class);
        DefaultFuture.recive(res);
    }

    private JSONObject getJSONObject(String str){
        JSONObject json = JSONObject.parseObject(str);
        json.remove("content");
        json.put("msg","保存用户信息成功");
        return json;
    }
}
