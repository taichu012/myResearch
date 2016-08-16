package taichu.research.network.netty4.tlvCodec.codec.wrapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import taichu.research.network.netty4.tlvCodec.codec.protocol.smp.SmpEncoderOnNetty4;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp;

public class Netty4EncoderWrapper extends MessageToByteEncoder<Smp> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Smp msg, ByteBuf out) throws Exception {
		out.writeBytes(IMyEncoder.genPack(msg));
	}
}
