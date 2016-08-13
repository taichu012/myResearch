package taichu.research.network.netty4.demotest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * Handler implementation for the server.
 */
@Sharable
public class MyNettyServerHandler extends ChannelInboundHandlerAdapter {
	
	private volatile long firstCallTime=System.currentTimeMillis();
	
    private int badMsgReceivedCount=0;
    private int goodMsgReceivedCount=0;
    private int goodMsgSentCount=0;
    private int badMsgSendCount=0;
    //由client控制发送数目来测试，server不控制，被动接受。
    //private volatile int MAX_SEND_MSG=50000;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	if (msg == null||"".equals(msg)) {
    		System.out.println("Got null msg!");
    		badMsgReceivedCount++;
    		return;
    	}else {
    		goodMsgReceivedCount++;
    	}
    	
    	System.out.println("==============channel-read==============");
    	System.out.println(msg.toString());

    	//send response back to client
    	//append line based 分界符
    	String response = getResponse();
    	if(response==null||"".equals(response)){
    		badMsgSendCount++;
    	}else {
    		ByteBuf resp = Unpooled.copiedBuffer(response.getBytes()); 
    		ctx.writeAndFlush(resp);
    	   	goodMsgSentCount++;
    	}
    	
    }
    
    private String getResponse(){
    	long currTimeMs=System.currentTimeMillis();
    	return "Server.sendtime.ms=["+currTimeMs
    	+"]=["+T.Time.getDateTimeNow(currTimeMs)+"]"
    	+"本行以rn结尾<如能解析为单独一行则说明rn被正确的用于行分解符了！>."+Delimiters.getLineDelimiterStrForWin();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("==============channel-read-complete==============");
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        printStat();
        ctx.close();
    }
    
    @Override
    public boolean isSharable() {
        System.out.println("==============handler-sharable==============");
        //TODO：此函数的用处和场景是什么？
        return super.isSharable();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-register==============");
        //TODO：推断在client连接上来后，就初始化一个channel并注册，等client关闭后，channel自动注销；
        System.out.println("Create instance of MyNettyServerHandler at: "+firstCallTime);
        firstCallTime=System.currentTimeMillis();
        System.out.println("channel-register at: "+firstCallTime);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-unregister==============");
        printStat();
        System.out.println("channel-unregister at: "+System.currentTimeMillis());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-active==============");
        //TODO：需要调查如果idle事件后，是否会在收到msg时重新再次avtive激活？推断理应如此。
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-inactive==============");
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
