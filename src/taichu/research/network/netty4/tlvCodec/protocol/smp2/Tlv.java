package taichu.research.network.netty4.tlvCodec.protocol.smp2;

import taichu.research.tool.T;

public final class Tlv {
	private byte SMP_DATA_TYPE = 0;
	private int SMP_DATA_LEN = 0;
	private byte[] SMP_DATA_VAL = null;//按需生成
	

	//构造函数
	Tlv(int type, byte[] value){
		this.SMP_DATA_TYPE = (byte)type;
		this.SMP_DATA_LEN = value.length;
		T.Type.byteArrayCopy(value, this.SMP_DATA_VAL);
		}
	
	public byte getSMP_DATA_TYPE() {
		return SMP_DATA_TYPE;
	}

	public int getSMP_DATA_LEN() {
		return SMP_DATA_LEN;
	}

	public byte[] getSMP_DATA_VAL() {
		return SMP_DATA_VAL;
	}


}
