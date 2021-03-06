package taichu.research.network.netty4.VehiclePassingRecordCollector.vpr;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.ISmp;

/**
 * Handler implementation for the server.
 * 
 */

public class VehiclePassingRecordReceiverHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger log = Logger.getLogger(VehiclePassingRecordReceiverHandler.class);
	private volatile long firstCallTime=System.nanoTime();
	
    private long badMsgReceivedCount=0l;
    private long goodMsgReceivedCount=0l;
    private long goodMsgSentCount=0l;
    private long bytes=0l;

    //netty触发的event的先后关系，详查netty自带javadoc的Interface ChannelHandlerContext

    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	//A Channel received a message
    	//log.debug("Got event ‘channelRead’.");

    	//因为前面的心跳协议过滤了HB/PING/PONG，流转到这里的必定是其他字符串；
    	try {
    		String m=msg.toString();
    		if (m.length()==0){
        		log.error("Got ONE EMPTY msg from client!");
    			badMsgReceivedCount++;
    			//最后一个inbound handler理应不需要对msg引用计数减一;后同！
    			//ReferenceCountUtil.release(msg);
    			return;
    		}else {
    			bytes+=m.getBytes().length;
    			goodMsgReceivedCount++;
    			
    	    	//handle ONE message ASAP.
    			//TODO:考虑将SMP改为（<8位MD5>+<json>)格式，则需要专门的handler或codec来处理；
    			//     届时需要同时考虑不同handler的位置和调整代码！！！
    	    	String msgid=handleMsg(m);
    	    	if ("".equals(msgid)){
    	    		log.warn("Parse msg error: got bad msgid!");
    	    		badMsgReceivedCount++;
    	    	}else {
    		    	//create response and send back to client as ACK
    	    		String resp=buildResponse(msgid);
    		    	ByteBuf respBB = Unpooled.copiedBuffer(resp.getBytes()); 
    		    	
    		    	ctx.writeAndFlush(respBB);
//    		    	log.debug("Server sent response to client,response=["+createResponse(msgid)+"].");
    		    	goodMsgSentCount++;
    		    	if (goodMsgSentCount%100000==0){
//    	    			log.debug(goodMsgSentCount+",send feedback["+rspmsg.toString()+"]");
    		    		printStat();
    	    		}

    	    	}
    		}
    	}catch (Exception e){
    		log.error("msg cannot convert toString()!");
    		badMsgReceivedCount++;
    		e.printStackTrace();
    		ReferenceCountUtil.release(msg); 		
    	}
   }

    /**
     * 
     * @param msg
     * @return msgid
     */
    private String handleMsg(String msg) {
 	
//		System.out.println("Got msg["+msg+"],length=["+msg.length()+"]");
		
		//TODO: Parse this message/record and resends to kafka as a producer ASAP！;
		//或者更复杂的是将消息扔到一个队列就马上返回，队列处理输入kafka的问题，但破坏了事务性，建议直接在这里输入kafka！
		
    	//如果第一个字符就是‘|’或找不到它，就返回空字符串；否则取回第一个竖杠的前面所有substring
    	if (msg.indexOf('|')<=0) {
       		log.warn("Parse msg error: can not find msgid.");
    		return ""; 
    	}else {	
    		return msg.substring(0, msg.indexOf('|'));
    	}
		
	}

	private String buildResponse(String msgid){
		StringBuilder resp= new StringBuilder();
		
		//response format please refer to protocol in VehiclePassingRecordBasedOnSmp
    	resp.append(msgid);//msgid got from client
    	resp.append(ISmp.EOS);//"|" as delimiter
    	resp.append(System.currentTimeMillis());//server nanotime for client
    	resp.append(ISmp.EOL); //line delimiter
    	
    	return resp.toString();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//    	//leave channelRead method
////    	log.debug("Got event ‘channelReadComplete’.");
//        ctx.flush();
//    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
    	log.error(cause.getMessage());
    	log.error(cause.getStackTrace());
    	cause.printStackTrace();
        printStat();
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

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	//A Channel was registered to its EventLoop
    	log.debug("Got event ‘channelRegistered’.");
        log.info(firstCallTime+"ns,"+"Instance of VehiclePassingRecordReceiverHandler is created!");
        firstCallTime=System.nanoTime();
        log.info(firstCallTime+"ns,"+"channel-registered!");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	//A Channel was unregistered from its EventLoop
    	log.debug("Got event ‘channelUnregistered’.");
        printStat();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//A Channel is active now, which means it is connected. 
    	log.debug("Got event ‘channelActive’.");
    	
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//A Channel is inactive now, which means it is closed. 
    	log.debug("Got event ‘channelInactive’.");
        printStat();
    }
    
    private void printStat(){
    	long deltaTime=System.nanoTime()-firstCallTime;
    	log.info("Total got MSG=" + goodMsgReceivedCount+", in "+(deltaTime)/1000/1000+"ms, AVG="
    			+ (float)goodMsgReceivedCount*1000*1000*1000/deltaTime + "CAPS, "
    			+bytes/1000/8*1000*1000*1000/deltaTime+"KBps.");
    	
    	log.info(", MSG received successful:"+ goodMsgReceivedCount
    			+", MSG received bad:"+badMsgReceivedCount
    			+",MSG sent successful:" + goodMsgSentCount
    			+", 空包率=坏包率="+(double)badMsgReceivedCount/goodMsgReceivedCount+"%");
    	
    	
    }
    
}
