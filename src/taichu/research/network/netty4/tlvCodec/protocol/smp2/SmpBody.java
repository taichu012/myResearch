package taichu.research.network.netty4.tlvCodec.protocol.smp2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import taichu.research.tool.T;


public final class SmpBody {
	private static Logger log = Logger.getLogger(SmpBody.class);
	/*****************************************
	 *********SMP BODY DEFINE***************
	 ******************************************/

	private int SMP_DATA_TOTAL = 0;//按需生成
	private static final byte[] SMP_BODY_RESERVED = new byte[]{0x00,0x00};
	private int SMP_BODY_CHKSUM = 0;
	private HashMap<String, Tlv> tlvs = new HashMap<String, Tlv>(); 
	//定义非协议功能字段
	private int totalBodyLen=0;
	private ByteBuffer bodybb = null;
	



	public SmpBody(HashMap<String, Tlv> iptTlvs){
		tlvs=iptTlvs;
		SMP_DATA_TOTAL=T.Type.intToBytes(tlvs.size(),1)[0];
		setTotalBodyLen(getBobyLen(tlvs));
		bodybb = ByteBuffer.allocate(getTotalBodyLen());
		SMP_BODY_CHKSUM=buildBodyBytesAndReturnBodyChksum(tlvs);
		bodybb.putShort((short) getSMP_BODY_CHKSUM());
	}

	private int buildBodyBytesAndReturnBodyChksum(HashMap<String, Tlv> tlvs) {
		bodybb.put(this.SMP_BODY_FLAG);
		Iterator<Entry<String, Tlv>> iter = tlvs.entrySet().iterator();
		bodybb.put((byte) this.SMP_DATA_TOTAL);
		bodybb.put(this.SMP_BODY_RESERVED);
		while (iter.hasNext()) {
			Map.Entry<String, Tlv> entry = (Map.Entry<String, Tlv>) iter.next();
			//获得一个tlv section
			Tlv tlv=entry.getValue();
			if (tlv==null){
				log.warn("TLV is null,just ignore."); 
				break;
			}else {
				//处理一个tlv小结
				bodybb.put(tlv.getSMP_DATA_TYPE());
				bodybb.putShort((short) tlv.getSMP_DATA_LEN());
				bodybb.put(tlv.getSMP_DATA_VAL());
			}
		}
		return T.Crc.Crc16_Algm2.genCrc16(bodybb.array());
	}
	
	private int getBobyLen(HashMap<String, Tlv> tlvs) {
		int bodyLen=0;
		Iterator<Entry<String, Tlv>> iter = tlvs.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Tlv> entry = (Map.Entry<String, Tlv>) iter.next();
			//获得一个tlv section
			Tlv tlv=entry.getValue();
			if (tlv==null){
				log.warn("TLV is null,just ignore."); 
				break;
			}else {
				bodyLen+=1+2+tlv.getSMP_DATA_VAL().length;
			}
		}
		bodyLen += 1 +1 +2 +2;
		return bodyLen;
	}

	private static final byte SMP_BODY_FLAG = 0x67;//不允许更改，协议固定值
	public int getSMP_DATA_TOTAL() {
		return SMP_DATA_TOTAL;
	}

	public int getSMP_BODY_CHKSUM() {
		return SMP_BODY_CHKSUM;
	}

	public static byte getSmpBodyFlag() {
		return SMP_BODY_FLAG;
	}

	public static byte[] getSmpBodyReserved() {
		return SMP_BODY_RESERVED;
	}

	public HashMap<String, Tlv> getTlvs() {
		return tlvs;
	}
	
	
	public int getTotalBodyLen() {
		return totalBodyLen;
	}

	public void setTotalBodyLen(int totalBodyLen) {
		this.totalBodyLen = totalBodyLen;
	}
	
	public ByteBuffer getBodyBytes() {
		return bodybb;
	}
}
