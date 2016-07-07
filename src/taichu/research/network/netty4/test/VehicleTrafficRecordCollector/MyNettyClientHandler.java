/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.F;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class MyNettyClientHandler extends ChannelInboundHandlerAdapter {

//    private final ByteBuf firstMessage;
    

    /**
     * Creates a client-side handler.
     */
    public MyNettyClientHandler() {
//        firstMessage = Unpooled.buffer(MyNettyClient.SIZE);
//        for (int i = 0; i < firstMessage.capacity(); i ++) {
//            firstMessage.writeByte((byte) i);
//        }
    }

    private String getNextMsg() { 
    	long currTimeMs=System.currentTimeMillis();
    	
    	//TODO：将下面\n改为从reord协议接口取；并测试如果当个\r的情况是否会错误呢？
    	return "ClientMSG-"+currTimeMs
    	+" @ "+F.GetF().getDateTimeFromCurrentTimeMillis(currTimeMs)
    	+IVehicleTrafficRecordLineBasedString.DelimiterStringAllowed.get("win_r_n")+"我前面是rn，我后面是n！"
    	+IVehicleTrafficRecordLineBasedString.DelimiterStringAllowed.get("linux_n");}
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	
    	String firstMsg = getNextMsg();
    	ByteBuf resp = Unpooled.copiedBuffer(firstMsg.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//    	long syscode = F.GetF().bytesToLong(msg);
//    	System.out.println("Server Return syscode=["+msg+"]");
    	//TODO:synCode can be inserted a map list for asynchronous produce!
    	String nextMsg = getNextMsg();
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ByteBuf resp = Unpooled.copiedBuffer(nextMsg.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
