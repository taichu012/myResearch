package taichu.research.network.netty4.tlvCodec.codec.protocol.smp2;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import taichu.research.network.netty4.tlvCodec.protocol.smp2.Smp;

public class Smp2EncoderOnNetty4 extends MessageToByteEncoder<Smp> {
	private static Logger log = Logger.getLogger(Smp2EncoderOnNetty4.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Smp msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.getSmpBytes());
	}
}
