package taichu.research.network.netty4.VehiclePassingRecordCollector;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.VehiclePassingRecordBasedOnSmp;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * Handler implementation for the server.
 * 
 */
@Sharable
public class VehiclePassingRecordReceiverHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger log = Logger.getLogger(VehiclePassingRecordReceiverHandler.class);
	private static final String PING=VehiclePassingRecordBasedOnSmp.HEARTBEAT_REQ;
	private volatile long firstCallTime=System.nanoTime();
	
    private int badMsgReceivedCount=0;
    private int goodMsgReceivedCount=0;
    private int goodMsgSentCount=0;
    private int badMsgSendCount=0;
    private long bytes=0l;

    //netty触发的event的先后关系，详查netty自带javadoc的Interface ChannelHandlerContext

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	//A Channel received a message
//    	log.debug("Got event ‘channelRead’.");
    	String oneMsg=null;
    	if (msg instanceof String) {
    		oneMsg=msg.toString();
    		bytes+=oneMsg.getBytes().length;
//    		log.debug("Got a msg from client,msg=["+oneMsg+"]，bytes["+oneMsg.getBytes().length+"]");
    		if (msg.toString().length()<=0){
    			badMsgReceivedCount++;
    			return;
    		}else {
    			goodMsgReceivedCount++;
    		}
    	}else {
//       		System.out.println("Got a bad msg!");
    		log.error("Got a bad msg from client!");
    		badMsgReceivedCount++;
    		return;
    	}
    	
    	//handle message/record ASAP.
    	String msgid=handleMsg(oneMsg);
    	if ("".equals(msgid)){
    		log.warn("Parse message error: got bad message id section!");
    		badMsgReceivedCount++;
    	}else {
	    	//create response and send back to client as ACK
	    	ByteBuf resp = Unpooled.copiedBuffer(createResponse(msgid).getBytes()); 
	    	
	    	ctx.writeAndFlush(resp);
//	    	log.debug("Server sent response to client,response=["+createResponse(msgid)+"].");
	    	goodMsgSentCount++;
	    	if (goodMsgSentCount%10000==0){
    			log.debug(goodMsgSentCount+",send feedback["+msgid.toString()+"]");
				ctx.writeAndFlush(Unpooled.copiedBuffer((PING+
						 Smp.EOL).toString().getBytes()));
				log.info("发送了一条PING.");
    		}
	    	
//	    	try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

    	}
    	
    }
    
    private String handleMsg(String msg) {
   	
    	//output to console for testing
//		System.out.println("Got msg["+msg+"],length=["+msg.length()+"]");
		
		//TODO: Parse this message/record and resends to kafka as a producer ASAP！;
		//或者更复杂的是将消息扔到一个队列就马上返回，队列处理输入kafka的问题，但破坏了事务性，建议直接在这里输入kafka！
		
    	//如果第一个字符就是‘|’或找不到它，就返回空字符串；否则取回第一个竖杠的前面所有substring
    	if (msg.indexOf('|')<=0) {
    		return "";
    	}else {	
    		return msg.substring(0, msg.indexOf('|'));
    	}
		
	}

	private String createResponse(String msgid){
		StringBuilder resp= new StringBuilder();
		
		//response format please refer to protocol in VehiclePassingRecordBasedOnSmp
    	resp.append(msgid);//msgid got from client
    	resp.append(Delimiters.Delimiter_verticalbar);//"|" as delimiter
    	resp.append(System.currentTimeMillis());//server nanotime for client
    	resp.append(Delimiters.getLineDelimiterStrForWin()); //line delimiter
    	
    	return resp.toString();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	//leave channelRead method
//    	log.debug("Got event ‘channelReadComplete’.");
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
    	log.error(cause.getMessage());
    	log.error(cause.getStackTrace());
    	cause.printStackTrace();
        printStat();
        ctx.close();
    }
    
    @Override
    public boolean isSharable() {
        //说明：http://my.oschina.net/xinxingegeya/blog/295577
    	//举例：http://www.myexception.cn/software-architecture-design/1256248.html
    	//推测：@Sharable是加载class前面的annotation，用来说明handler是可以被反复利用，反复放在pool中的。
    	//    否则应该就用1个线程1次性使用，并考虑合理退出exit机制，而不应该用多线程或pool机制；
    	//    当client断开连接后，handler被回收等待再利用，此时没有sharable就会报错。
    	
    	log.debug("Got event ‘isSharable’.");
        return super.isSharable();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	//A Channel was registered to its EventLoop
    	log.debug("Got event ‘channelRegistered’.");
        //TODO：推断在client连接上来后，就初始化一个channel并注册，等client关闭后，channel自动注销；
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
    			+ (float)goodMsgReceivedCount*1000*1000*1000/deltaTime + "CAPS, "+bytes/1000/8+"KBps.");
    	
    	log.info("MSG sent successful:" + goodMsgSentCount
    			+", MSG sent bad:"+ badMsgSendCount
    			+", MSG received successful:"+ goodMsgReceivedCount
    			+", MSG received bad:"+badMsgReceivedCount
    			+", 空包率=坏包率="+(double)badMsgReceivedCount/goodMsgReceivedCount+"%");
    	
    	
    }
    
}
