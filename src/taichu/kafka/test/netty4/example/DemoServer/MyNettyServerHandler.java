package taichu.kafka.test.netty4.example.DemoServer;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyNettyServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("MyNettyServer: got EVENT[channelRead],read NOW!");
//		System.out.println("server channelRead..");
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		//TODO:上面行有个优化的方案，从几十万纳秒提升到9000纳秒，找一下release notes是否有link或百度一下
		System.out.println("MyNettyServer: receive msg:" + body);
		String currentTime = "QUERY TIME REQUEST".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
				: "BAD REQUEST";
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(resp);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();// 刷新后才将数据发出到SocketChannel
		System.out.println("MyNettyServer: got EVENT[channelReadComplete],flush NOW!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		System.out.println("MyNettyServer: got EVENT[exceptionCaught],close CONTEXT.");

	}

}