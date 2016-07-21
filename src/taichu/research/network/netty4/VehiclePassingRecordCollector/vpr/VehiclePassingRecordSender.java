package taichu.research.network.netty4.VehiclePassingRecordCollector.vpr;

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
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.Smp;
import taichu.research.network.netty4.VehiclePassingRecordCollector.smp.VehiclePassingRecordBasedOnSmp;
import taichu.research.tool.Delimiters;
import taichu.research.tool.IniReader;
import taichu.research.tool.T;

/**
 * Base on netty. Connect to server as long keep alive socket/TCP, send
 * msg/record with unified RID(for handling) after a small sleep server send
 * response (included MSGID) and info client for ACK.
 * 
 * client->server detail protocal:refer to SMP
 * server->client detail protocal:refer to SMP
 * 
 * TODO: netty's multi threads topic.
 * 
 */
public final class VehiclePassingRecordSender {

	private static Logger log = Logger.getLogger(VehiclePassingRecordSender.class);
	private static String CONFIG_INI_FILENAME="D:\\resource\\git\\MyResearch\\src\\"
			+"taichu\\research\\network\\netty4\\VehiclePassingRecordCollector\\config.ini";
	private final static IniReader conf= new IniReader(CONFIG_INI_FILENAME);
	
	static final boolean SSL = System.getProperty("ssl") != null;
//	static final String HOST = System.getProperty("host", "127.0.0.1");
//	static final int PORT = Integer.parseInt(System.getProperty("port", "9923"));
	
	static final String HOST = conf.getValue("vprc.server.config", "vprc.server.ip");
	static final int PORT = Integer.parseInt(conf.getValue(
			"vprc.server.config", "vprc.server.port"));
	
	static final int SIZE = VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH;

	// 如果Handler 被标记为‘@Sharable’，则可以重用本地变量到不同pipeline等请，而不用每次new一个实例
    // protected static final SmpHeartbeatHandler heartbeatHandler = new SmpHeartbeatHandler();

	public static Bootstrap bootstrap = null;
	public static Channel channel = null;

	/**	 
	 * 初始化Bootstrap	 
	 * @return Bootstrap
	*/	
	public static final Bootstrap getBootstrap(){
        // Configure SSL
        final SslContext sslCtx;
        if (SSL) {
            try {
				sslCtx = SslContextBuilder.forClient()
				    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
				e.printStackTrace();
				log.error("Init error, prepare ssl context error, exit!");
				return null;
			}
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             //资料：浅谈tcp_nodelay的作用 ；（http://stephen830.iteye.com/blog/2109006）
             .option(ChannelOption.TCP_NODELAY, true)
             //.option(ChannelOption.SO_BACKLOG, 1) //TODO:client 不能用？待研究！！！
             //提示：netty框架的 keepAlive属性，不能设置间隔，会采用TCP协议默认的配置2小时，程序可自行增加控制心跳超时机制
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
                     //打开log可能较慢，仅用于第业务量的开发测试阶段；
                     //p.addLast(new LoggingHandler(LogLevel.ERROR));
                     
                     //顺序定义了INBOUND入栈（server->client)的消息解析handler，按addLast定义的顺序处理；
                     
                     //解决TCP正常可能偶发的粘包问题; 
                     //获得接口指定的line分界符；支持server可能为WIN/LINUX(UNIX)/MAC三种操作系统的三类不同的line分界符；
                     //ByteBuf delimiter_win = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForWin());
                     //ByteBuf delimiter_linux = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForLinux());
                     //ByteBuf delimiter_mac = Unpooled.copiedBuffer(Delimiters.getLineDelimiterBytesForMac());
                     //hint：netty框架中，拆分粘包可用如下decoder，组装消息发出去前则需要自己添加line分界符；
                     //为decoder添加多个分界符，用来自动拆分粘包；
//                     p.addLast(new DelimiterBasedFrameDecoder(VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH,
//                    		 true,false,delimiter_win,delimiter_linux,delimiter_mac));
                   
                     p.addLast(new DelimiterBasedFrameDecoder(VehiclePassingRecordBasedOnSmp.MSG_LINE_MAX_LENGTH,
            		 true,false,Unpooled.copiedBuffer(Smp.EOL.getBytes())));
                     
                     p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                      
                     //添加netty框架自带的控制读超时，写超时告警handler.
                     p.addLast(new IdleStateHandler(Smp.READ_IDEL_TIMEOUT_S,
                    		 Smp.WRITE_IDEL_TIMEOUT_S, Smp.ALL_IDEL_TIMEOUT_S, TimeUnit.SECONDS)); // 

                     p.addLast("vprReceiverHandler", new VehiclePassingRecordSenderHandler());
                     
                     //添加自定义的处理读，写，空闲超时的handler.
                     //此超时检测心跳的handler不参与消息具体业务处理。
                     p.addLast("heartBeatHandler", new SmpHeartbeatHandler()); 

                     //如下定义了OUTBOUND入栈（client->server)的消息层层包装，是按addLast定义顺序的“逆序”来处理；
                     //这里没有定义OUTBOUND，直接在MyNettyClientHandler中自己出了发送writeAndFlush，无需过多handler逻辑！
                 }

             });
            return b;
        }

	public static final Channel getChannel(String host, int port) {
		Channel channel = null;
		try {
			// Start the client.
			channel = bootstrap.connect(host, port).sync().channel();
 			//ChannelFuture f=bootstrap.connect(host, port).sync();
 			//channel=f.channel();
			log.info(String.format("连接到Server(IP[%s],PORT[%s])成功！", host, port));
		} catch (Exception e) {
			log.error(String.format("连接到Server(IP[%s],PORT[%s])失败！", host, port), e);
			return null;
		}
		return channel;
	}

	public static void main(String[] args) throws Exception {
		try {
			log.info("client try to start...");
			if ((bootstrap = getBootstrap())==null){
				log.error("client started failed by cannot init bootstrap!");
			};
			if ((channel=getChannel(HOST, PORT))==null){
				log.error("client started failed by cannot init channel!");
			}
			log.info("client started and got connection with server.");
			
			//处理业务，比如发送部分消息，测试发送消息，或永久发送消息等等；
			RunMe(channel);
			
			//发完消息也不关闭连接
			//下面这句等待client关闭，如果两端都不关闭，则互相等待，处理心跳和idle等。
			channel.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("client got error, will be closed.");
		}finally{
			//一般如果不是优雅关闭，或用future关闭，则需要等待片刻！让数据包走完，
			//否则pipeline两端一旦拆除，可能导致少量包丢失。
			log.info("client sleep 3000ms for a while.");
			Thread.sleep(3000);

			//优雅关闭
			// Shut down the event loop to terminate all threads.
			bootstrap.config().group().shutdownGracefully();
			log.info("client closed.");
		}
	}
	
	private static String genMsg(){
    	String csvFile=conf.getValue("vprc.client.config", "vprc.client.msg.csvfile");
    	
    	ConcurrentHashMap<String, String> linesMap = T.File.getLinesWithMD5KeyFromFile(csvFile);
    	StringBuffer message=new StringBuffer(); 
    	
    	for(Entry<String, String> line: linesMap.entrySet() ){ 
	    	//根据SMP协议，section1放入MD5校验值，后面添加上其他sections
	    	//添加MD5值为头
	    	message.append(line.getKey());
	    	message.append(Smp.EOS);
	    	//转换value中的csv的逗号为竖杠“|”
	    	message.append((line.getValue()).replace(',','|'));
	    	message.append(Smp.EOL);
    	}
    	return message.toString();
	}
	
	private static ByteBuf bbMsg = null;
	
	private static void RunMe(Channel channel) throws Exception{

		long t0 = System.nanoTime();
		long MAX_RUN=10000000000l;
		long count =0l;
		
		//TEST: how long msg is suitable
		StringBuilder sb=new StringBuilder();
		//测试将消息重复拷贝几次并组装为一个长字符串
		int copyTimes=1;
		for (int i=0; i<copyTimes; i++){
			sb.append(genMsg());
		}
		    	
		bbMsg = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
						sb.toString(),CharsetUtil.UTF_8));

		
    	while (count<=200000){

    		//send message
    		//use sync() to Waits for this future until it is done, 
    		//and rethrows the cause of the failure if this future failed.
    		channel.writeAndFlush(bbMsg.duplicate()).sync();
    		
    		//do not use sync() to return immediately
    		//注意！如果不用sync()命令则使用Unpooled.unreleasableBuffer内存时会引起内存溢出
    		//是因为内存不是放的关系，一下子堆起来，然后快速发送；可以将count调大来触发错误，报错如下：
    		//“OutOfDirectMemoryError: failed to allocate 16777216 byte(s) of direct memory (used: 251658240, max: 259522560)”
    		//另外，如果用Unpooled.copiedBuffer内存，则引起“ Failed to fail the promise because it's done already” ERROR!
    		//又另外，直接通过非bytebuf而是byte[]字节数组传过去，对方无法接收，一个也接收不到！
//    		channel.writeAndFlush(bbMsg);
    		
    		//send a very small smp, just for test templately
//    		channel.writeAndFlush(Unpooled.copiedBuffer("a|b\r\n".getBytes())).sync();
    		

    		//每一定间隔输出统计信息
			if (count%100000==0) {
				long delta=System.nanoTime() - t0;
		    	log.info("Total sent MSG=" + count+", in "+(delta)/1000/1000+"ms, AVG="
		    			+ (float)count*1000*1000*1000/delta 
		    			+ "CAPS, "+(sb.toString().toString().getBytes().length*count)/1000/8*1000*1000*1000/delta +"KBps.");

			}
			count++;
    	}

	}


}
