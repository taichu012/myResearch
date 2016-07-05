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
package taichu.kafka.test.netty4.example.discardServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// discard

		// netty author said：Please keep in mind that this method will be
		// renamed to messageReceived
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// 需要编解码的才会去用messageReceived，一般都是使用ChannelRead来读取的。读一下
		// SimpleChannelInboundHandler的源代码你就知道了，泛型不匹配，不会调用messageReceived的。
		//
		//
		//
		// 另：如果你特别特别想用SimpleChannelInboundHandler，你可以这样搞：public
		// classYouTCPServerHandler extends
		// SimpleChannelInboundHandler<ByteBuf>{...}
		//
		// 因为你没有做过任何的编码解码，所以你的泛型是ByteBuf，这样你肯定可以使用messageReceived来接收到消息了。如果还不明白，建议你去看一下netty自带的sample，里面有个求阶乘的例子，server和client传递的BigInteger对象，所以就用的是
		//
		// SimpleChannelInboundHandler<BigInteger>。没有经过任何编码解码的那就肯定是ByteBuf对象

		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) { // (1)
				System.out.println((char) in.readByte());
				//TODO:你这里可以用print不换行，我换行是为了看到底收到的是几个字符；
				//测试发现，telnet客户端每次按下英文字母就以字节发到server被netty处理；
				//如果telnet客户都打的是中文比如”中国“，则在client端会一下子发过来4个字节（GBK是2字节1个字符）
				//所以你看到了server这里应对”中国“的是4行输出；
				//！！！我们要做的就是将一次读取完毕后的bytes都整合为byte[]字节数组，然后转码为utf-8或gbk输出到console
				//这样就看得到中文了。
//				byte[] bytes= in.readByte();
//				System.out.print(new String((byte[])in.readByte(),"utf-8"));
//				String s  =new String(s.getBytes("gbk"),"utf-8");
				System.out.flush();
				// TODO:同样也有jmeter或telnet不关闭导致的read阻塞一直等着，需要协商END标记
			}
		} finally {
			ReferenceCountUtil.release(msg); // (2)
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
