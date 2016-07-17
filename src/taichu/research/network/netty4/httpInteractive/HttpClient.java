package taichu.research.network.netty4.httpInteractive;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import taichu.research.tool.T;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.net.URI;

import org.apache.log4j.Logger;

public class HttpClient {
	
    private static Logger log = Logger.getLogger(HttpClient.class);
    
	public void connect(String host, int port) throws Exception {


		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);//关键是这句
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					// 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
					ch.pipeline().addLast(new HttpResponseDecoder());
					// 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
					ch.pipeline().addLast(new HttpRequestEncoder());
					ch.pipeline().addLast(new HttpClientHandler());
				}
			});

			// Start the client.
			ChannelFuture f = b.connect(host, port).sync();

			URI uri = new URI("http://127.0.0.1:9924");
			String msg = "";
			// DefaultFullHttpRequest request = new
			// DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
			// uri.toASCIIString(),
			// Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

			// 构建http请求
			// request.headers().set(HttpHeaderNames.HOST, host);
			// request.headers().set(HttpHeaderNames.CONNECTION,
			// HttpHeaderValues.KEEP_ALIVE);
			// request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
			// request.content().readableBytes());
			DefaultFullHttpRequest request = null;
			long count=0l;
			long t0 = System.nanoTime();
			while (count<1000000000) {
				msg = "MSGID=" + count + "," + T.getT().getDateTimeFromCurrentTimeMillis();
				//use unpooled bytebuf
				request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),
						Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
				
				// 构建http请求
				request.headers().set(HttpHeaderNames.HOST, host);
				request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
				// 发送http请求
				f.channel().write(request);
				if (count%10000==0){
					f.channel().flush();
					Thread.sleep(500);
				}
				
	    		//每一定间隔输出统计信息
				if (count%10000==0) {
					long delta=System.nanoTime() - t0;
			    	log.info("Total sent MSG=" + count+", in "+(delta)/1000/1000+"ms, AVG="
			    			+ (float)count*1000*1000*1000/delta 
			    			+ "CAPS, "+(msg.toString().toString().getBytes().length*count)/1000/8*1000*1000*1000/delta +"KBps.");

				}
				count++;
			}
			f.channel().flush();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) throws Exception {
		HttpClient client = new HttpClient();
		client.connect("127.0.0.1", 9924);
	}
}
