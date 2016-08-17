package taichu.research.network.netty4.tlvCodec.protocol.smp2;

import java.nio.ByteBuffer;

public final class SmpHead {
	/*****************************************
	 *********SMP HEADER DEFINE***************
	 ******************************************/
	
	private static final byte SMP_FLAG = 0x66;//不允许更改，协议固定值
	private static final byte SMP_VER = 0x10;//不允许更改，协议固定值
	private static final int SMP_HEAD_LEN = 16;
	//public static final byte[] SMP_HEAD_LEN = T.Type.intToBytes(0x11,2); 不允许更改，协议固定值
	private int SMP_MSG_ID = 0;//按需生成
	private int SMP_BODY_LEN = 0;//按需生成
	private static final byte[] SMP_HEAD_RESERVED = new byte[]{0x00,0x00,0x00};
	private byte SMP_HEAD_CHKSUM = 0x00;//按需生成
	
	public int getSMP_MSG_ID() {
		return SMP_MSG_ID;
	}
	public void setSMP_MSG_ID(int sMP_MSG_ID) {
		SMP_MSG_ID = sMP_MSG_ID;
	}
	public int getSMP_BODY_LEN() {
		return SMP_BODY_LEN;
	}
	public void setSMP_BODY_LEN(int sMP_BODY_LEN) {
		SMP_BODY_LEN = sMP_BODY_LEN;
	}
	public byte getSMP_HEAD_CHKSUM() {
		return SMP_HEAD_CHKSUM;
	}
	public void setSMP_HEAD_CHKSUM(byte sMP_HEAD_CHKSUM) {
		SMP_HEAD_CHKSUM = sMP_HEAD_CHKSUM;
	}
	public static byte getSmpFlag() {
		return SMP_FLAG;
	}
	public static byte getSmpVer() {
		return SMP_VER;
	}
	public static int getSmpHeadLen() {
		return SMP_HEAD_LEN;
	}
	public static byte[] getSmpHeadReserved() {
		return SMP_HEAD_RESERVED;
	}

	
}
