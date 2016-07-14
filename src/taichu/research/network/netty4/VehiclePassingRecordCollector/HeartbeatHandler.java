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

@Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static Logger log = Logger.getLogger(HeartbeatHandler.class);
	private static final String HB=VehiclePassingRecordBasedOnSmp.HEARTBEAT_CHARS;
	
	// Return a unreleasable view on the given ByteBuf
	// which will just ignore release and retain calls.
	private static final ByteBuf BBuf_HB = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer(
					HB+Smp.EOL,CharsetUtil.UTF_8));  // 1

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
			ctx.writeAndFlush(BBuf_HB.duplicate()).addListener(
					ChannelFutureListener.CLOSE_ON_FAILURE);  // 3
			System.out.println(ctx.channel().remoteAddress()+"超时类型：" + type);
			//IdleStateEvent消息到此为止，不在流转！
			
			//TODO:考虑在这里关闭整个pipeline，否则pipeline一直活着。超时心跳模块决定pipeline的是否关闭close！
			
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	log.debug("Got event ‘channelRead’.");
    	
    	if (msg instanceof String){
    		if (HB.equals(msg.toString())){
    			log.info("Got HB and ensure peer side is alive! Do nothing!");
    		}else{
    			//其他字节或字符串一律不做处理，留待后面的handler继续处理；
    			ctx.fireChannelRead(msg);
    		}
    	}else {
    		//其他字节或字符串一律不做处理，留待后面的handler继续处理；
    		ctx.fireChannelRead(msg);
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
    
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	log.debug("Got event ‘channelRegistered’.");
    	ctx.fireChannelRegistered();
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	log.debug("Got event ‘channelUnregistered’.");
    	ctx.fireChannelUnregistered();
    }
    	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//A Channel is active now, which means it is connected. 
    	log.debug("Got event ‘channelActive’.");
    	ctx.fireChannelActive();
    	
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//A Channel is inactive now, which means it is closed. 
    	log.debug("Got event ‘channelInactive’.");
    	ctx.fireChannelInactive();

    }
	

}
