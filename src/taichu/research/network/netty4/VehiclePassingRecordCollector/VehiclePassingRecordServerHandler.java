package taichu.research.network.netty4.VehiclePassingRecordCollector;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * Handler implementation for the server.
 * TODO：消息event的先后关系，详查netty自带javadoc的Interface ChannelHandlerContext
 */
@Sharable
public class VehiclePassingRecordServerHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger log = Logger.getLogger("VehiclePassingRecordServerHandler.class");
	private volatile long firstCallTime=System.currentTimeMillis();
	
    private int badMsgReceivedCount=0;
    private int goodMsgReceivedCount=0;
    private int goodMsgSentCount=0;
    private int badMsgSendCount=0;
    //由client控制发送数目来测试，server不控制，被动接受。
    //private volatile int MAX_SEND_MSG=50000;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	//A Channel received a message
    	log.debug("Got event ‘channelRead’.");
    	
    	if (msg instanceof String) {
    		log.debug("Got a msg from client,msg=["+msg.toString()+"]");
    		goodMsgReceivedCount++;
    	}else {
//       		System.out.println("Got a bad msg!");
    		log.debug("Got a bad msg from client!");
    		badMsgReceivedCount++;
    		return;
    	}
    	
    	//handle message/record ASAP.
    	String msgid=handleMsg(msg.toString());
    	if ("".equals(msgid)){
    		log.warn("Parse message error: got bad message id section!");
    	}else {
	    	//create response and send back to client as ACK
	    	String response = createResponse(msgid);
	    	if(!"".equals(response)){
	    		ByteBuf resp = Unpooled.copiedBuffer(response.getBytes()); 
	    		ctx.writeAndFlush(resp);
	    		log.debug("Server sent response to client,response=["+response+"].");
	    	   	goodMsgSentCount++;
	    	}else {
	    		badMsgSendCount++;
	    	}
    	}
    	//TODO: stopping here!
    	
    }
    
    private String handleMsg(String msg) {
    	String msgid="";
    	
    	//output to console for testing
		System.out.println("Got msg["+msg+"],length=["+msg.length()+"]");
		
		//TODO: Parse this message/record and resends to kafka as a producer ASAP！;
		//或者更复杂的是将消息扔到一个队列就马上返回，队列处理输入kafka的问题，但破坏了事务性，建议直接在这里输入kafka！
		
		return msgid;
		
	}

	private String createResponse(String msgid){
		StringBuilder resp= new StringBuilder();
		
		//response format please refer to protocol in VehiclePassingRecordLineBasedString
    	resp.append(msgid);//msgid got from client
    	resp.append(Delimiters.Delimiter_verticalbar);//"|" as delimiter
    	resp.append(System.nanoTime());//server nanotime for client
    	resp.append(Delimiters.getLineDelimiterStrForWin()); //line delimiter
    	
    	return resp.toString();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	//leave channelRead method
    	log.debug("Got event ‘channelReadComplete’.");
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
    	log.error(cause.getMessage());
    	log.error(cause.getStackTrace());
//        printStat();
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
        System.out.println("Create instance of MyNettyServerHandler at: "+firstCallTime);
        firstCallTime=System.currentTimeMillis();
        System.out.println("channel-register at: "+firstCallTime);
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
//        printStat();
    }
    
    private void printStat(){
    	long currentTime=System.currentTimeMillis();
    	long deltaTime=currentTime-firstCallTime;
    	System.out.println("Total got MSG=" + goodMsgReceivedCount+", spent="+(deltaTime)/1000+"秒, AVG="
    			+ goodMsgReceivedCount*1000/deltaTime + "CAPS");
    	
    	System.out.println("MSG sent successful:" + goodMsgSentCount
    			+", MSG sent bad:"+ badMsgSendCount
    			+", MSG received successful:"+ goodMsgReceivedCount
    			+", MSG received bad:"+badMsgReceivedCount);
    	
    	
    }
    
}
