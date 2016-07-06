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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

/**
 * Echoes back any received data from a client.
 */
public final class MyNettyServer {
	
	//定义消息的格式，TODO：之后放入专门的消息class做一个源码层面的说明更好！

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "9923"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             //what is BACKLOG(http://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html#bind(java.net.SocketAddress,%20int))
             .option(ChannelOption.SO_BACKLOG, 15)
//             .option(ChannelOption.SO_BACKLOG, 100)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }

//                     //解决TCP粘包问题,以"$_"作为分隔  
//                     String delimiterStr = IVehicleTraffiecRecordDelimiterBasedString.DelimiterAllowedString;
//                     ByteBuf delimiter = Unpooled.copiedBuffer(delimiterStr.getBytes());  
//                     p.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));  

                  // Decoders added as with order
                     
                     //LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast) 
                     //stripDelimiter=false就不会将分隔符去掉而让用户自己处理；
                     //javadoc中API说对\n（linux只有回车），\r\n(win是换行回车都有)会处理，
                     //但不会对\r（mac os只有换行）做处理
                     //调查：查看class（LineBasedFrameDecoder）的155行如下，的确没有处理单独用\r作为分解符的场景
                     //“private static int findEndOfLine(final ByteBuf buffer) {”
                     //http://netty.io/4.0/api/io/netty/handler/codec/LineBasedFrameDecoder.html
                     p.addLast("frameDecoder",new LineBasedFrameDecoder(IVehicleTrafficRecord.MSG_LINE_MAX_LENGTH
                    		 ,false,false));
                     
                     //Decodes a received ByteBuf into a String. Please note that this decoder 
//                     must be used with a proper ByteToMessageDecoder such as DelimiterBasedFrameDecoder 
//                     or LineBasedFrameDecoder if you are using a stream-based transport such as TCP/IP. 
                     //作用，将上一个decoder通过分隔符返回的bytes组装成string，否则输出的是不可用的信息！
                     p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                     
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(new MyNettyServerHandler());
                     
                 }
             });

            // Start the server.
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
