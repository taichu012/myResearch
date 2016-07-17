package taichu.research.network.netty4.httpInteractive;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;
import taichu.research.tool.T;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

	private static Logger log = Logger.getLogger(HttpServerHandler.class);
	private long goodReceivedCount=0l;
    private HttpRequest request;
    long t0=System.nanoTime();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

    	long t0=System.nanoTime();
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

            String uri = request.uri(); 
//            System.out.println("HttpServer收到HttpClient发来的请求URI=" + uri);
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
//            ByteBuf buf = content.content();
            ByteBuf buf = Unpooled.wrappedBuffer(content.content());
            
            String msgstr=buf.toString(CharsetUtil.UTF_8);
			if ((System.nanoTime()-t0) % 100 == 0) {
				System.out.println("HttpClient说："+msgstr);
			}
            buf.release();

            goodReceivedCount++;
            
            String res = "HttpServer返回客户端消息【"+msgstr+"】，server时间"
            		+T.getT().getDateTimeFromCurrentTimeMillis();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                    OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
            if (HttpUtil.isKeepAlive(request)) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }
            ctx.writeAndFlush(response);
//            ctx.write(response);
//            ctx.flush();
            printStat();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
    
    private void printStat() {
		//每一定间隔输出统计信息
		if (goodReceivedCount%10000==0) {
			long delta=System.nanoTime() - t0;
	    	log.info("Total sent MSG=" + goodReceivedCount+", in "+(delta)/1000/1000+"ms, AVG="
	    			+ (float)goodReceivedCount*1000*1000*1000/delta 
	    			+ "CAPS.");

		}

    }

}
