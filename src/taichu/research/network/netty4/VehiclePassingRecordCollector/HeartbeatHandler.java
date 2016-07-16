/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.VehiclePassingRecordBasedOnSmp;

/**
 * @author taichu
 *
 */


public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = Logger.getLogger(HeartbeatHandler.class);
	private static final String TIMEOUT_CONFIG_STR="read超时"+Smp.READ_IDEL_TIMEOUT_S
			+"秒，write超时"+Smp.WRITE_IDEL_TIMEOUT_S
			+"秒，all超时"+Smp.ALL_IDEL_TIMEOUT_S;

	private static final ByteBuf ByteBuf4PING = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer(
					Smp.HEARTBEAT_PING+Smp.EOL,CharsetUtil.UTF_8));  
	
	private static final ByteBuf ByteBuf4PONG = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer(
					Smp.HEARTBEAT_PONG+Smp.EOL,CharsetUtil.UTF_8)); 
	
	// Return a unreleasable view on the given ByteBuf
	// which will just ignore release and retain calls.
	private static final ByteBuf ByteBuf4HB = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer(
					Smp.HEARTBEAT_HB+Smp.EOL,CharsetUtil.UTF_8));  // 1

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {  // 2
			IdleStateEvent event = (IdleStateEvent) evt;  
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
			//当捕获到三类超时后，发送心跳协议，不要求对端回应（无定义，无要求），只要本发送成功，
			//就说明TCP长连接保活机制（ChannelOption.SO_KEEPALIVE）是有效且在2小时内（默认），
			//当前client和server之间的TCP链路通过TCP协议自己的心跳维持且双向互通正常OK，可用！
			//如果发送失败，则得知TCP链路损毁，需拆除和善后；
			//TODO：此处直接在发送失败后拆除链路，是否会级联触发其他event？归于善后处理（数据保存等）的机会？待查 TODO//
			ctx.writeAndFlush(ByteBuf4HB.duplicate()).addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE); 
			ctx.writeAndFlush(ByteBuf4PING.duplicate()).addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE);  
			log.warn((ctx.channel().remoteAddress()+"超时类型：" + type+","+
					TIMEOUT_CONFIG_STR));
			//IdleStateEvent消息到此为止，不在流转！
			
			//TODO:考虑在这里关闭整个pipeline，否则pipeline一直活着。超时心跳模块决定pipeline的是否关闭close！
			
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	log.debug("Got event ‘channelRead’.");
    	
    	if(!(msg instanceof String)){
    		//其他字节或字符串一律不做处理，触发后面handler继续read；
    		ctx.fireChannelRead(msg);
    	}else {
    		String str=msg.toString();
    		
       		if (Smp.HEARTBEAT_HB.equals(str)){
    			log.info("Got 'HB'2chars and ensure peer side is alive! Do nothing!");
    			//中断消息处理，不让其他handle处理
    			ctx.fireChannelReadComplete();
    		}else if (Smp.HEARTBEAT_PING.equals(str)){
    			//自定义的PING,PONG心跳处理器（逻辑心跳，不是TCP协议自动实现的心跳）
    			log.info("Got PING from peer side and sent PONG back, if send failed, channel will be closed!");
    			ctx.writeAndFlush(ByteBuf4PONG.duplicate()).addListener(
    					ChannelFutureListener.CLOSE_ON_FAILURE);
//    			TODO：是否能补充一个自定义event时间，当收到PONG的时候认为OK，否则就CLOSED通道channel.
    			//中断消息处理，不让其他handle处理
    			ctx.fireChannelReadComplete();   		
    		}else if(Smp.HEARTBEAT_PONG.equals(str)){
    			log.info("Got PONG from peer side and ensure it is alive!");
    			//TODO:怎么确认这个PONG是上一个PING呢？待完善！
    			//中断消息处理，不让其他handle处理
    			ctx.fireChannelReadComplete(); 
    		}else {
    			//其他字节或字符串一律不做处理，触发后面handler继续read；
    			ctx.fireChannelRead(msg);
    		}
    	}
    }
    
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//    	log.info("Got event 'channelReadComplete'.");
//       ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.debug("Got event ‘exceptionCaught’, channel will be closed.");
    	log.error(cause.getMessage());
    	log.error(cause.getStackTrace());
    	cause.printStackTrace();
        ctx.close();
    }
    
//    @Override
//    public boolean isSharable() {
//        //说明：http://my.oschina.net/xinxingegeya/blog/295577
//    	//举例：http://www.myexception.cn/software-architecture-design/1256248.html
//    	//推测：@Sharable是加载class前面的annotation，用来说明handler是可以被反复利用，反复放在pool中的。
//    	//    否则应该就用1个线程1次性使用，并考虑合理退出exit机制，而不应该用多线程或pool机制；
//    	//    当client断开连接后，handler被回收等待再利用，此时没有sharable就会报错。
//    	
//    	log.debug("Got event ‘isSharable’.");
//        return super.isSharable();
//    }
    
 //TODO:等待验证，register与否，active与否，如果不继承和override，系统会自动按照pipeline继续channel chain吗？
    
//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//    	log.debug("Got event ‘channelRegistered’.");
//    	ctx.fireChannelRegistered();
//    }
//    
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//    	log.debug("Got event ‘channelUnregistered’.");
//    	ctx.fireChannelUnregistered();
//    }
//    	
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//    	//A Channel is active now, which means it is connected. 
//    	log.debug("Got event ‘channelActive’.");
//    	ctx.fireChannelActive();
//    	
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//    	//A Channel is inactive now, which means it is closed. 
//    	log.debug("Got event ‘channelInactive’.");
//    	ctx.fireChannelInactive();
//
//    }
	

}
