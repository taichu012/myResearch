package taichu.research.network.netty4.httpInteractive;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

	private static final long t0=System.nanoTime();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpResponse) 
        {
            HttpResponse response = (HttpResponse) msg;
//            System.out.println("HttpServer发来的response中CONTENT_TYPE=" + response.headers().get(
//            		HttpHeaderNames.CONTENT_TYPE));
        }
        if(msg instanceof HttpContent)        {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			long delta=System.nanoTime()-t0;
			if (delta % 10000 == 0) {
				System.out.println(delta+"|HttpServer说： " + buf.toString(CharsetUtil.UTF_8));
			}
			buf.release();
        }
    }
}