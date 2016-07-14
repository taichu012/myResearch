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

public class PingPongHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger log = Logger.getLogger(PingPongHandler.class);
	private static final String PING=VehiclePassingRecordBasedOnSmp.HEARTBEAT_REQ;
	private static final String PONG=VehiclePassingRecordBasedOnSmp.HEARTBEAT_RSP;
	private static final ByteBuf BBuf_PONG = Unpooled
			.unreleasableBuffer(Unpooled.copiedBuffer(
					PONG+Smp.EOL,CharsetUtil.UTF_8));  // 1
	   
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	log.debug("Got event ‘channelRead’.");
    	
    	if (msg instanceof String){
    		if (PING.equals(msg.toString())){
    			log.info("Got PING and sent PONG to peer side. if transfer failed, channel will be closed!");
    			ctx.writeAndFlush(BBuf_PONG.duplicate()).addListener(
    					ChannelFutureListener.CLOSE_ON_FAILURE);
//    			TODO：是否能补充一个自定义event时间，当收到PONG的时候认为OK，否则就CLOSED通道channel.
    			//中断消息处理
    		}else if(PONG.equals(msg.toString())){
    			log.info("Got PONG and ensure peer side is alive!");
    			//TODO:怎么确认这个PONG是上一个PING呢？待完善！
    		}else {
    			//非PING/PONG心跳，则继续处理下去；
    			//触发下一个handler先激活，如果需要，可以做些准备；
    			//ctx.fireChannelActive();
    			
    			//route to next handler
    			ctx.fireChannelRead(msg);
    		}
    	}else {
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
