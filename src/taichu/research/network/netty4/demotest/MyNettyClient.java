package taichu.research.network.netty4.demotest;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.VehiclePassingRecordBasedOnSmp;
import taichu.research.tool.Delimiters;

/**
 * 连接到server，发送msg，当收到server反馈则发送下一条;
 * client->server 接口：详见IVehicleTrafficRecordLineBasedString
 * server->client 接口：只返回系统时间，但line分界符类似接口IVehicleTrafficRecordLineBasedString；
 * 
 */
public final class MyNettyClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "9923"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .option(ChannelOption.SO_BACKLOG, 15)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     
                     //顺序定义了INBOUND入栈（server->client)的消息解析，是按addLast的顺序处理；
                     //处理路径：server msg->DelimiterBasedFrameDecoder->StringDecoder->MyNettyClientHandler->完成最终消费
                     
                     //解决TCP正常可能偶发的粘包问题; 
                     //获得接口指定的line分界符；支持server可能为WIN/LINUX(UNIX)/MAC三种操作系统的三类不同的line分界符；
                     ByteBuf delimiter_win = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForWin());
                     ByteBuf delimiter_linux = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForLinux());
                     ByteBuf delimiter_mac = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForMac());
                     //hint：netty框架中，拆分粘包可用如下decoder，组装消息发出去前则需要自己添加line分界符；
                     //为decoder添加多个分界符；
                     p.addLast(new DelimiterBasedFrameDecoder(VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH,
                    		 true,false,delimiter_win,delimiter_linux,delimiter_mac));  
                     p.addLast(new StringDecoder());  
                     p.addLast(new MyNettyClientHandler());
                     
                     //如下定义了OUTBOUND入栈（client->server)的消息层层包装，是按addLast的“逆序”来处理；
                     //所谓“逆序”，是先定义到addLast的最后处理；
                     //这里没有定义OUTBOUND，直接在MyNettyClientHandler中自己出了发送writeAndFlush，无需过多handler逻辑！
                 }
             });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
