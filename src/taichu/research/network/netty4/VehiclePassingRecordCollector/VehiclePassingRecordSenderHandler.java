package taichu.research.network.netty4.VehiclePassingRecordCollector;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * 实现消息的处理
 */
public class VehiclePassingRecordSenderHandler extends ChannelInboundHandlerAdapter {

	private static Logger log = Logger.getLogger(VehiclePassingRecordSenderHandler.class);

	
    private int badMsgReceivedCount=0;
    private int goodMsgReceivedCount=0;
    /**
     * Creates a client-side handler.
     */
    public VehiclePassingRecordSenderHandler() {

    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	log.info("Got event 'channelActive'.");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	log.info("Got event 'channelRead'.");

    	if (!(msg instanceof String)) {
    		log.warn("Got null msg!");
    		badMsgReceivedCount++;
    		return;
    	}else if(msg.toString().length()==0){
    		log.warn("Got empty msg!");
    		badMsgReceivedCount++;
    		return;
    	}else {
    		goodMsgReceivedCount++;
    		if (goodMsgReceivedCount%100000==0){
//    			log.debug(goodMsgReceivedCount+",Got feedback["+msg.toString()+"]");
    			printStat();
//				ctx.writeAndFlush(Unpooled.copiedBuffer((PING+
//						 Smp.EOL).toString().getBytes()));
//				log.info("发送了一条PING.");
//				ctx.writeAndFlush(Unpooled.copiedBuffer((HB+
//						 Smp.EOL).toString().getBytes()));
//    			log.info("Send HB and ensure peer side is alive! Do nothing!");
		    	}
    		}
    	}
   	
    	
    	//TODO:server返回的msg可以作为对client发来message的处理请求的response，也可作为异步处理。
    	//这里是同步处理了。必须要有server返回，则才能让client触发下一条消息的发送；
    	//如果异步处理，就要涉及两边模块对消息状态的管控，包括超时等处理。万一server对某条msg处理30秒才返回，
    	//client作为超时并重发了，导致server多处理了一遍就不好了。 是个难点；



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//    	log.info("Got event 'channelReadComplete'.");
//       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
    	log.error("Got event 'exceptionCaught'.");
        cause.printStackTrace();
        printStat();
        ctx.close();

    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.info("Got event 'channelInactive'.");
        printStat();
        ctx.close();
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	log.info("Got event 'channelRegistered'.");    	
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	log.info("Got event 'channelUnregistered'.");   
        printStat();
    }
    
    private void printStat(){
    	System.out.println("MSG received successful:"+ goodMsgReceivedCount
    			+", MSG received bad:"+badMsgReceivedCount
    			+", 空包率=坏包率="+(double)badMsgReceivedCount/goodMsgReceivedCount+"%");
    	
    }
}
