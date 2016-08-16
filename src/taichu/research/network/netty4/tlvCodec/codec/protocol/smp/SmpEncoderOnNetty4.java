package taichu.research.network.netty4.tlvCodec.codec.protocol.smp;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp.Tlv;
import taichu.research.tool.T;

public class SmpEncoderOnNetty4 extends MessageToByteEncoder<Smp> {
	private static Logger log = Logger.getLogger(SmpEncoderOnNetty4.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Smp msg, ByteBuf out) throws Exception {
		outputMsg(out,msg);
	}
    private static final Random rand = new Random();
    //private final int flags = 0xdeadbeef; //=4bytes/8个char
    
	public static void outputMsg(ByteBuf out, Smp smp){
		
		//处理HEAD
		ByteBuffer headbb = ByteBuffer.allocate(16);

		headbb.put(Smp.getSmpFlag());// is fixed field;
		headbb.put(Smp.getSmpVer());// is fixed field;
		headbb.putShort((short) Smp.getSmpHeadLen());// is fixed field;
		
		//用基本不重复的ms毫秒级时间做种子；再去int全集范围的随机数，碰撞概率就很小；
		//因为MSG_ID用于收发桩模块的消息核对，一般在几分钟内应该确认并使用完毕，就可以丢弃；（只要几分钟内不重复就可）
		rand.setSeed(System.currentTimeMillis());
		smp.setSMP_MSG_ID(rand.nextInt());
		headbb.putInt(smp.getSMP_MSG_ID());
		
		int bodyLen=getBobyLen(smp);
		smp.setSMP_BODY_LEN(bodyLen);//需要获得所有BODY字段后，最后计算；
		headbb.putInt(smp.getSMP_BODY_LEN());
		headbb.put(Smp.getSmpHeadReserved());
		smp.setSMP_HEAD_CHKSUM(T.Crc.CRC8_Algm1.genCrc8(headbb.array()));
		headbb.put(smp.getSMP_HEAD_CHKSUM());
		
		//输出HEAD
		out.writeBytes(headbb);

		
		//处理BODY
		ByteBuffer bodybb = ByteBuffer.allocate(bodyLen);
		
		bodybb.put(smp.getSmpBodyFlag());
		
		HashMap<String, Tlv> tlvs=smp.getTlvs();
		Iterator<Entry<String, Tlv>> iter = tlvs.entrySet().iterator();
		bodybb.put((byte) tlvs.size());
		bodybb.put(smp.getSmpBodyReserved());
		while (iter.hasNext()) {
			Map.Entry<String, Tlv> entry = (Map.Entry<String, Tlv>) iter.next();
			//获得一个tlv section
			Tlv tlv=entry.getValue();
			if (tlv==null){
				log.warn("TLV is null,just ignore."); 
				break;
			}else {
				//处理一个tlv小结
				bodybb.put(tlv.SMP_DATA_TYPE);
				bodybb.putShort((short) tlv.SMP_DATA_LEN);
				bodybb.put(tlv.SMP_DATA_VAL);
			}
		}
		smp.setSMP_BODY_CHKSUM(T.Crc.Crc16_Algm2.genCrc16(bodybb.array()));
		bodybb.putShort((short) smp.getSMP_BODY_CHKSUM());
		
		
		//输出BODY
		out.writeBytes(bodybb);

		}

	
	private static int getBobyLen(Smp smp) {
		// TODO Auto-generated method stub
		return 0;
	}


}
