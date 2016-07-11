package taichu.research.network.netty4.test.VehiclePassingRecordCollector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * 实现消息的处理
 */
public class MyNettyClientHandler extends ChannelInboundHandlerAdapter {

    private int badMsgReceivedCount=0;
    private int goodMsgReceivedCount=0;
    private int goodMsgSentCount=0;
    private int badMsgSendCount=0;
    private static  int MAX_SEND_MSG=6000000;

    /**
     * Creates a client-side handler.
     */
    public MyNettyClientHandler() {    }

    //准备以当前时间为主的不断变化的字符串
    private String getNextMsg() { 
    	long currTimeMs=System.currentTimeMillis();

//    	//以下包含三种OS的不同行结束分界符，用来测试，结果符合预期。实测其他功能时，最好来回消息一一对应，不要1对N，不方便观察。
//    	return "Client.sendtime.ms=["+currTimeMs
//    	+"]=["+T.GetF().getDateTimeFromCurrentTimeMillis(currTimeMs)+"]"
//    	+"本行以rn结尾<如能解析为单独一行则说明rn被正确的用于行分解符了！>."+Delimiters.getLineDelimiterStrForWin()
//    	+"本行以   n结尾<如能解析为单独一行则说明   n被正确的用于行分解符了！>："+Delimiters.getLineDelimiterStrForLinux()
//    	+"本行以   r结尾<如能解析为单独一行则说明   r被正确的用于行分解符了！>："+Delimiters.getLineDelimiterStrForMac()
//    	+"本行以rn结尾<如能解析为单独一行则说明rn被正确的用于行分解符了！>："+Delimiters.getLineDelimiterStrForWin();}
    	
    	return "Client.sendtime.ms=["+currTimeMs
    	+"]=["+T.getT().getDateTimeFromCurrentTimeMillis(currTimeMs)+"]"
    	+"本行以rn结尾<如能解析为单独一行则说明rn被正确的用于行分解符了！>."+Delimiters.getLineDelimiterStrForWin();
    	}
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	ByteBuf resp = Unpooled.copiedBuffer(getNextMsg().getBytes());
        ctx.writeAndFlush(resp);
        goodMsgSentCount++;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
   	

    	if (msg == null||"".equals(msg)) {
    		System.out.println("Got null msg!");
    		badMsgReceivedCount++;
    		return;
    	}else {
    		goodMsgReceivedCount++;
    	}
    	

    	//经过INBOUND处理链上先后的分包和string化，根据协议，server返回的时间ms可直接作为string打印；
//    	System.out.println("Server.sendtime.ms=["+msg+"]=["
//    	+T.GetF().getDateTimeFromCurrentTimeMillis(Long.parseLong((String) msg))+"]");
    	System.out.println(msg.toString());
    	
    	
    	//TODO:server返回的msg可以作为对client发来message的处理请求的response，也可作为异步处理。
    	//这里是同步处理了。必须要有server返回，则才能让client触发下一条消息的发送；
    	//如果异步处理，就要涉及两边模块对消息状态的管控，包括超时等处理。万一server对某条msg处理30秒才返回，
    	//client作为超时并重发了，导致server多处理了一遍就不好了。 是个难点；

    	//TODO:设定手动停止标记，比eclipse强制关闭更好些！
    	if (goodMsgSentCount>MAX_SEND_MSG) {
    		System.out.println("Close client after sent "+MAX_SEND_MSG+" msgs!");
//            printStat();
    		ctx.close();
    		return;
    	}
    	
//    	try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	String response = getNextMsg();

    	if(response==null||"".equals(response)){
    		badMsgSendCount++;
    	}else{
        	ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
    		ctx.writeAndFlush(resp);
    		goodMsgSentCount++;
    	}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-inactive==============");
//        printStat();
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-register==============");

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-unregister==============");
        printStat();
        System.out.println("channel-unregister at: "+System.currentTimeMillis());
    }
    
    private void printStat(){
    	System.out.println("MSG sent successful:" + goodMsgSentCount
    			+", MSG sent bad:"+badMsgSendCount
    			+", MSG received successful:"+ goodMsgReceivedCount
    			+", MSG received bad:"+badMsgReceivedCount);
    	
    }
}
