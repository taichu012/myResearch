/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.vpr;

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
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.VehiclePassingRecordBasedOnSmp;

/**
 * @author taichu
 *
 */


public class SmpHeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = Logger.getLogger(SmpHeartbeatHandler.class);
	private static final String TIMEOUT_LOG_STR="read timeout："+Smp.READ_IDEL_TIMEOUT_S
			+"s, write timeout:"+Smp.WRITE_IDEL_TIMEOUT_S
			+"s, all idle timeout:"+Smp.ALL_IDEL_TIMEOUT_S;

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
					Smp.HEARTBEAT_HB+Smp.EOL,CharsetUtil.UTF_8)); 

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
			//当捕获到三类超时后，发送SMP的2种心跳协议，HB不要求对端回应，PINGPONG可以但不强制回应。
			//一般的，只要发送成功，就说明TCP长连接保活机制（ChannelOption.SO_KEEPALIVE）是有效且在2小时内（默认），
			//心跳机制也可额外增加逻辑层的其他自定义要求，不再累赘。
			//如果发送失败，则得知TCP链路损毁，netty框架的future异步监听会拆除connection，关闭pipeline；
			//TODO：如果需要在netty框架拆除链路之前做“善后处理”，则需要额外增加代码；
			//programExit();
			ctx.writeAndFlush(ByteBuf4HB.duplicate()).addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE); 
			ctx.writeAndFlush(ByteBuf4PING.duplicate()).addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE);  
			log.warn((ctx.channel().remoteAddress()+"timeout for：" + type+","+
					TIMEOUT_LOG_STR));
			//IdleStateEvent消息到此为止，不再流转！
			
			//当捕获超时后打印信息，并按需发送HB消息；这里参考SMP协议做法，保持TCP长链接，超时候发送2种心跳之一或全部；
			//如果不参考SMP协议，通常的做法也很可能是探测到timeout的一端主动关闭connection；
			
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	log.debug("Got event ‘channelRead’.");
    	
    	if(!(msg instanceof String)){
    		//其他字节或字符串一律不做处理，触发后面handler继续read；
//    		ctx.fireChannelRead(msg);
    		log.warn("心跳消息解析错误：流转到最后的非业务消息，也不是心跳消息，所以错误！ msg不是字符串.");
    	}else {
    		String str=msg.toString();
    		
       		if (Smp.HEARTBEAT_HB.equals(str)){
    			log.info("Got 'HB'2chars and ensure peer side is alive! Do nothing!");
    			//中断消息处理，不让其他handle处理
//    			ctx.fireChannelReadComplete();
    		}else if (Smp.HEARTBEAT_PING.equals(str)){
    			//自定义的PING,PONG心跳处理器（逻辑心跳，不是TCP协议自动实现的心跳）
    			log.info("Got PING from peer side and sent PONG back, if send failed, channel will be closed!");
    			ctx.writeAndFlush(ByteBuf4PONG.duplicate()).addListener(
    					ChannelFutureListener.CLOSE_ON_FAILURE);
    			//TODO：是否能补充一个自定义event事件，用异步future事件监听listener机制来监听
    			//是否到PONG的回复，没收到就优雅退出.如果定义了就不需要下面对PONG的接收机制了。
    			//中断消息处理，不让其他handle处理
//    			ctx.fireChannelReadComplete();   
    		}else if(Smp.HEARTBEAT_PONG.equals(str)){
    			log.info("Got PONG from peer side and ensure it is alive!");
    			//中断消息处理，不让其他handle处理
//    			ctx.fireChannelReadComplete(); 
    		}else {
    			//其他字节或字符串一律不做处理，触发后面handler继续read；
//    			ctx.fireChannelRead(msg);
    			log.warn("心跳消息解析错误：流转到最后的非业务消息，也不是心跳消息，所以错误！ msg=["+msg+"].");
    		}
    	}
    }
    
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//    	log.info("Got event 'channelReadComplete'.");
       ctx.flush();
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
