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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import taichu.research.tool.F;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class MyNettyServerHandler extends ChannelInboundHandlerAdapter {
	
	private volatile long firstCallTime=System.currentTimeMillis();
	private volatile int gotMsgCount=0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	gotMsgCount++;
    	
    	System.out.println("==============channel-read==============");
    	System.out.println(msg.toString());
//    	ctx.write(msg);
    	String serverResponse=Long.toString(System.currentTimeMillis());
    	serverResponse +=IVehicleTrafficRecordLineBasedString.DelimiterStringAllowed.get("win_r_n");
    	ByteBuf resp = Unpooled.copiedBuffer(serverResponse.getBytes()); 
    	ctx.writeAndFlush(resp);
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
        ctx.close();
    }
    
    @Override
    public boolean isSharable() {
        System.out.println("==============handler-sharable==============");
        return super.isSharable();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-register==============");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-unregister==============");
        printStat();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-active==============");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-inactive==============");
        printStat();
    }
    
    private void printStat(){
    	long currentTime=System.currentTimeMillis();
    	long deltaTime=currentTime-firstCallTime;
    	System.out.println("Totcal got MSG=" + gotMsgCount+", spent="+(deltaTime)/1000+"秒, AVG="
    			+ gotMsgCount*1000/deltaTime + "CAPS");
    	
    }
    
}
