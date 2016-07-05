package taichu.kafka.test.netty4.example.DemoServer;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyNettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(MyNettyClientHandler.class.getName());

    private final ByteBuf firstMessage;

    public MyNettyClientHandler() {
        byte[] req = "QUERY TIME REQUEST".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与服务端建立连接后
        ctx.writeAndFlush(firstMessage);
        System.out.println("MyNettyClilent: got EVENT[channelActive],msg is sent.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //服务端返回消息后
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
//        System.out.println("Now is :" + body);
        System.out.println("MyNettyClilent: got EVENT[channelRead],Server got REQUEST at "+body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("MyNettyClilent: got EVEMT[exceptionCaught].");
        // 释放资源
        logger.warning("Unexpected exception from downstream:"
                + cause.getMessage());
        ctx.close();
    }

}