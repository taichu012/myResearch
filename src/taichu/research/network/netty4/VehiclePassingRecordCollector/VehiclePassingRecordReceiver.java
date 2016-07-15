package taichu.research.network.netty4.VehiclePassingRecordCollector;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.VehiclePassingRecordBasedOnSmp;
import taichu.research.tool.Delimiters;

/**
 * Base on netty.
 * Connected by client as long keep alive socket/TCP,
 * receive MSG/record with unified RID(for handling) and consume it.
 * server send response (included RID) back to client for ACK.
 * 
 * client->server detail protocal:refer toVehiclePassingRecordLineBasedString
 * server->client detail protocal:refer toVehiclePassingRecordLineBasedString
 * 
 * TODO:
 * 1. netty's timeout topic.
 * 2. netty's multi thread topic.
 * 3. need a vehiclePassingRecordSend的配置INI文件，配置端口port等；
 * 4. netty's log/log4j handling
 * 
 */
public final class VehiclePassingRecordReceiver {
	private static Logger log = Logger.getLogger(VehiclePassingRecordReceiver.class);
	
    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "9923"));
//  static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
  static final int SIZE = VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH;
  
	//Handler MUST be marked with ‘@Sharable’, then can be used as local variable and 
	//maybe reused(isSharable() event will be triggered if it is shared) for different pipeline!
	protected static final HeartbeatHandler heartbeatHandler = new HeartbeatHandler();

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
             .option(ChannelOption.SO_BACKLOG, 1)
             .option(ChannelOption.TCP_NODELAY, true)
             //netty框架的 keepAlive属性，不能设置间隔，会采用系统默认的配置2小时，程序里可进一步在应用层设定
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .option(ChannelOption.SO_SNDBUF, 32*1024)
             .option(ChannelOption.SO_RCVBUF, 32*1024)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }
//                     p.addLast(new LoggingHandler(LogLevel.INFO));//TODO:打开快不快？

                     //设定channel inbounds pipeline，按allLast添加的顺序处理；
                     //先解析client发来的line based消息（line frame分包），再将byte[]组成string，提供给handler
                     
                     //Decoders added with order
                    
                     //http://netty.io/4.0/api/io/netty/handler/codec/LineBasedFrameDecoder.html
                     //client发来的MSG是line based，分界符可能是三种OS的，所以都尝试处理；
                     ByteBuf delimiter_win = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForWin());
                     ByteBuf delimiter_linux = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForLinux());
                     ByteBuf delimiter_mac = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForMac());

                     p.addLast(new DelimiterBasedFrameDecoder(VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH,
                    		 true,false,delimiter_win,delimiter_linux,delimiter_mac));  

                     
                     //Decodes a received ByteBuf into a String. Please note that this decoder 
//                     must be used with a proper ByteToMessageDecoder such as DelimiterBasedFrameDecoder 
//                     or LineBasedFrameDecoder if you are using a stream-based transport such as TCP/IP. 
                     //将上一个decoder通过分隔符返回的bytes组装成string，否则输出的是不可视的字节信息！
                     p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                     
                     //添加netty框架自带的控制读超时，写超时告警handler！
                     //此超时检测并发送单向心跳的handler不参与消息具体业务处理。
                     p.addLast(new IdleStateHandler(Smp.READ_IDEL_TIMEOUT_S,
                    		 Smp.WRITE_IDEL_TIMEOUT_S, Smp.ALL_IDEL_TIMEOUT_S, TimeUnit.SECONDS)); // 

                     //添加自定义的处理读，写，空闲超时的handler.
                     p.addLast("heartBeatHandler", heartbeatHandler); 
                     p.addLast("vprReceiverHandler",new VehiclePassingRecordReceiverHandler());
                     
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
