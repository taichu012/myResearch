package taichu.research.network.netty4.VehiclePassingRecordCollector;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.VehiclePassingRecordBasedOnSmp;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * Base on netty. Connect to server as long keepalive socket/TCP, send
 * msg/record with unified RID(for handling) after a small sleep server send
 * response (inclued RID) and info client for ACK.
 * 
 * client->server detail protocal:refer toVehiclePassingRecordLineBasedString
 * server->client detail protocal:refer toVehiclePassingRecordLineBasedString
 * 
 * TODO: 1. netty's timeout topic. 2. netty's multithread topic. 3. need a
 * vehiclePassingRecordSend的配置INI文件，配置端口port等；4.细化本类！
 * 
 */
public final class VehiclePassingRecordSender {

	private static Logger log = Logger.getLogger(VehiclePassingRecordSender.class);

	static final boolean SSL = System.getProperty("ssl") != null;
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "9923"));
	// static final int SIZE = Integer.parseInt(System.getProperty("size",
	// "256"));
	static final int SIZE = VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH;

	// Handler MUST be marked with ‘@Sharable’, then can be used as local
	// variable and
	// maybe reused(isSharable() event will be triggered if it is shared) for
	// different pipeline!
	protected static final HeartbeatHandler heartbeatHandler = new HeartbeatHandler();

	public static Bootstrap bootstrap = getBootstrap();
	public static Channel channel = getChannel(HOST, PORT);
	public static ChannelFuture f=null;
//	public static ChannelFuture channelFuture = getChannelFuture(HOST, PORT);

	/**	 
	 * 初始化Bootstrap	 
	 * @return	 
	*/	
	public static final Bootstrap getBootstrap(){
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            try {
				sslCtx = SslContextBuilder.forClient()
				    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("Init error, prepare ssl context error, exit!");
				return null;
			}
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
//        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             //浅谈tcp_nodelay的作用 ；（http://stephen830.iteye.com/blog/2109006）
             .option(ChannelOption.TCP_NODELAY, true)
//             .option(ChannelOption.SO_BACKLOG, 1) //TODO:client 不能用？
             //netty框架的 keepAlive属性，不能设置间隔，会采用系统默认的配置2小时，程序里怎么自己设置？TODO：
             .option(ChannelOption.SO_KEEPALIVE, true)
             .option(ChannelOption.SO_SNDBUF, 32*1024)
             .option(ChannelOption.SO_RCVBUF, 32*1024)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                     }
//                     p.addLast(new LoggingHandler(LogLevel.INFO));//TODO:打开快不快？
                     
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
                     p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                     
                     
                     //添加netty框架自带的控制读超时，写超时告警handler.
                     p.addLast(new IdleStateHandler(Smp.READ_IDEL_TIMEOUT_S,
                    		 Smp.WRITE_IDEL_TIMEOUT_S, Smp.ALL_IDEL_TIMEOUT_S, TimeUnit.SECONDS)); // 
                     //添加自定义的处理读，写，空闲超时的handler.
                     //此超时检测心跳的handler不参与消息具体业务处理。
                     p.addLast("heartBeatHandler", new HeartbeatHandler()); 
                     p.addLast("vprReceiverHandler", new VehiclePassingRecordSenderHandler());
                     
                     //如下定义了OUTBOUND入栈（client->server)的消息层层包装，是按addLast的“逆序”来处理；
                     //所谓“逆序”，是先定义到addLast的最后处理；
                     //这里没有定义OUTBOUND，直接在MyNettyClientHandler中自己出了发送writeAndFlush，无需过多handler逻辑！
                 }

             });
            
//
////            ChannelFuture f;
//			try {
//	            // Start the client.
//				f = b.connect(HOST, PORT).sync();
//	            // Wait until the connection is closed.
////	            f.channel().closeFuture().sync();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				group.shutdownGracefully();
//				log.error("connect server failed!");
//			}
           
            return b;
        }

	public static final Channel getChannel(String host, int port) {
		Channel channel = null;
		try {
			// Start the client.
			channel = bootstrap.connect(host, port).sync().channel();
//			ChannelFuture f=bootstrap.connect(host, port).sync();
//			channel=f.channel();
//			
			// Wait until the connection is closed.
//			channel.closeFuture().sync();
			log.info(String.format("连接Server(IP[%s],PORT[%s])成功！", host, port));
		} catch (Exception e) {
			log.error(String.format("连接Server(IP[%s],PORT[%s])失败！", host, port), e);
			return null;
		}
		return channel;
	}
	
//	public static final ChannelFuture getChannelFuture(String host, int port) {
//		ChannelFuture f = null;
//		try {
//			// Start the client.
//			f = bootstrap.connect(host, port).sync();
//			// Wait until the connection is closed.
//			f.channel().closeFuture().sync();
//		} catch (Exception e) {
//			log.error(String.format("连接Server(IP[%s],PORT[%s])失败", host, port), e);
//			return null;
//		}
//		return f;
//	}

	public static void sendMsg(String msg) throws Exception {
		if (channel != null) {
//			ByteBuf resp = Unpooled.copiedBuffer(msg.toString().getBytes());
//			f.channel().writeAndFlush(resp).sync();
			channel.writeAndFlush(Unpooled.copiedBuffer((msg).toString().getBytes())).sync();
		} else {
			log.warn("消息发送失败,连接尚未建立!");
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			log.info("client started.");
			RunForever();
			//发完消息并不关闭连接
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("client error.");
		}finally{
			//一般如果不是优雅关闭，或用future关闭，则一般需要等待片刻！让数据包走完，
			//否则pipeline两顿一旦拆除，可能导致少量包丢失。
//			Thread.sleep(3000);
			
			//下面这句等待client关闭，如果两端都不关闭，则互相等待，处理心跳和idle等。
			channel.closeFuture().sync();
			
			//优雅关闭
			// Shut down the event loop to terminate all threads.
			bootstrap.config().group().shutdownGracefully();
			log.info("client closed.");
		}
	}
	
	public static void RunForever() throws Exception{
    	String csvFilename = "D:\\SourceRemote\\git\\MyResearch\\src\\taichu\\research\\network\\netty4\\VehiclePassingRecordCollector\\VehiclePassingRecordDemo.csv";
    	ConcurrentHashMap<String, String> linesMap = T.getT().file.getLinesWithMD5KeyFromFile(csvFilename);
    	StringBuffer message=new StringBuffer(); 

		long t0 = System.nanoTime();
		long MAX_RUN=10000000000l;
		long count =0l;
    	
    	while (count<=200000){
    	for(Entry<String, String> line: linesMap.entrySet() ){ 
	    	//根据SMP协议，section1放入MD5校验值，后面添加上其他sections
	    	//添加MD5值为头
	    	message.append(line.getKey());
	    	message.append(Smp.EOS);
	    	//转换value中的csv的逗号为竖杠“|”
	    	message.append((line.getValue()).replace(',','|'));
	    	message.append(Smp.EOL);
//	        log.debug("gen 1 message=["+message.toString()+"]"); 
    	}
    		//send message
    		VehiclePassingRecordSender.sendMsg(message.toString());

			if (count%100000==0) {
				long delta=System.nanoTime() - t0;
		    	log.info("Total sent MSG=" + count+", in "+(delta)/1000/1000+"ms, AVG="
		    			+ (float)count*1000*1000*1000/delta 
		    			+ "CAPS, "+(message.toString().getBytes().length*count)/1000/8*1000*1000*1000/delta +"KBps.");

			}
    		//清空message容器
    		message.delete(0,message.length());
			count++;
    	}

	}


}
