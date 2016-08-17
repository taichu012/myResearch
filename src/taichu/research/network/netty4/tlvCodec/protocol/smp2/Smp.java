package taichu.research.network.netty4.tlvCodec.protocol.smp2;
import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.log4j.Logger;

import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public class Smp {

	private static Logger log = Logger.getLogger(Smp.class);
	
	private SmpHead smpHead=null;
	private SmpBody smpBody=null;
	
	//定义非协议功能字段
	private ByteBuffer headbb = ByteBuffer.allocate(16);
	private ByteBuffer smpbb = null;

	//只允许在smp消息实例构造的时候初始化设定tlv字段一次;
	//根据SMP协议，tlv字段的发送是不保证顺序的！
	public Smp(SmpHead smpH, SmpBody smpB){
		smpHead=smpH;
		smpBody=smpB;
		smpHead.setSMP_BODY_LEN(smpBody.getTotalBodyLen());
		smpHead.setSMP_HEAD_CHKSUM(buildHeadBytesAndReturnHeadChksum(smpHead));
		headbb.put(smpHead.getSMP_HEAD_CHKSUM());
		smpbb = ByteBuffer.allocate(headbb.capacity()+smpBody.getTotalBodyLen());
		smpbb.put(smpbb);
		smpbb.put(smpBody.getBodyBytes());
	}

	public ByteBuffer getSmpBytes() {
		return smpbb;
	}
	

	private static final Random rand = new Random();
	
	private byte buildHeadBytesAndReturnHeadChksum(SmpHead smpHead) {
		//处理HEAD
		headbb.put(smpHead.getSmpFlag());// is fixed field;
		headbb.put(smpHead.getSmpVer());// is fixed field;
		headbb.putShort((short) smpHead.getSmpHeadLen());// is fixed field;
		
		//用基本不重复的ms毫秒级时间做种子；再去int全集范围的随机数，碰撞概率就很小；
		//因为MSG_ID用于收发桩模块的消息核对，一般在几分钟内应该确认并使用完毕，就可以丢弃；（只要几分钟内不重复就可）
		rand.setSeed(System.currentTimeMillis());
		smpHead.setSMP_MSG_ID(rand.nextInt());
		headbb.putInt(smpHead.getSMP_MSG_ID());

		headbb.putInt(smpHead.getSMP_BODY_LEN());
		headbb.put(smpHead.getSmpHeadReserved());
		return T.Crc.CRC8_Algm1.genCrc8(headbb.array());
	}

	public SmpHead getSmpHead() {
		return smpHead;
	}

	public SmpBody getSmpBody() {
		return smpBody;
	}
	
	

}
