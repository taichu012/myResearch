package taichu.research.network.netty4.tlvCodec.protocol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import taichu.research.network.netty4.tlvCodec.core.Byte;

/**
 * 说明：
 * 1.此文件根据协议来定义数据载体；
 * 2.根据协议固定不变的字段（比如flag，reserved等字段）都需用static和final修饰；
 * 3.用Annotation（@Byte）描述字段实际含有多少字节（详见标注接口定义），便于后续编解码codec处理；
 * 4.字段实际定义的容器（byte，int，boolean，long等）首先要能足够包含字段在协议中定义的字节数，然后也可便于逻辑操作；
 * 4a.举例：对1，2，4字节，因为java中无对应class，所以用容器类int来包含字段内容；
 * 4b.举例：对8字节，用long作为容器类；
 * 4c.举例：对不定长字段（字节数未知），则可用String或byte[]做容器类，具体看怎么方便；
 * 4d.举例：有逻辑（是/否）含义的，可用boolean作为容器类；
 * 4e.举例:不在协议中定义的字段，但为了编程过程方便，可适当增加字段，比如“hasExtras”来标记是否有扩展信息，但实际协议没这个字段；
 * 5.字段用（@Byte）标注，并用足够且合适的容器类表示，最终用writeXXX写出到网络字节流中去；
 * 5a.举例：根据需求，字段是1个byte的，容器类是int，用writeByte将int最低字节写到网络字节流中去；
 * 5b.举例：根据需求，字段是2/4/8个bytes的，容器类是int/int/long，用writeShort/writeInt/writeLong写；
 * 5c.举例：根据字段定义和容器类的实际情况灵活处理；比如boolean可writeByte写1个字节出去；String/byte[]可用writeBytes()
 * 
 * @author taichu
 *
 */
public class Smp {

	private static Logger log = Logger.getLogger(Smp.class);
	
	/*****************************************
	 *********SMP HEADER DEFINE***************
	 ******************************************/
	
	//java的byte是-127到128，无法表达0x88（10001000），为程序方便将0x88存放到int的4字节最低字节，
	//当写入网络字节流的时候使用函数“writeByte(int)"则会将4字节int的低8位作为1个byte写入网络，并字节增1，
	//***详见annotation“Byte”的定义及说明；
	@Byte //1字节为默认可不写出
	public static final int SMP_FLAG = 0x88;//不允许更改，magic number
	@Byte
	public static final int SMP_VER = 0x10;//占1字节，用int4字节容器装值，writeByte写网络1字节
	@Byte(2)
	private int SMP_HEAD_LEN = 0; //被动计算值
	@Byte(4)
	private int SMP_MSG_ID = (int)System.nanoTime(); //TODO：考虑怎么使用随机数
	@Byte(4)
	private int SMP_BODY_LEN = 0; //被动计算值；占2字节，用int4字节容器装值，writeShort写网络2字节
	@Byte(3)
	public static final byte[] SMP_HEAD_RESERVED = new byte[]{0x00,0x00,0x00};
	@Byte
	private int SMP_HEAD_CHKSUM = 0x00;//被动计算值；占4字节，用int4字节容器装值，writeInt写网络4字节
	
	/*****************************************
	 *********SMP BODY DEFINE***************
	 ******************************************/
	
	@Byte
	public static final int SMP_BODY_FLAG = 0x87;//不允许更改，magic number
	@Byte
	private int SMP_DATA_TOTAL = 0;
	@Byte(2)
	public static final byte[] SMP_BODY_RESERVED = new byte[]{0x00,0x00,0x00};
	
	//定义DataType标识符
	public static enum DATA_TYPE_ENUM{DT_STRING,DT_BYTES};
	//定义DataType标识符匹配的值（参考SMP协议是int型）
	@SuppressWarnings("serial")
	public static final HashMap<DATA_TYPE_ENUM, Integer> SmpDataTypeDef =
				new HashMap<DATA_TYPE_ENUM, Integer>(){
			{put(DATA_TYPE_ENUM.DT_STRING, 0x01);
			 put(DATA_TYPE_ENUM.DT_BYTES, 0x02);}}; 
	
	
	//根据SMP协议规定，TLV Section可重复多个；TLVCodec应能动态计算；
	//根据SMP协议，并未定义TLV Section必须有固定次序，所以用HaspMap，可不排序，不保证次序；
	private HashMap<String, TlvSection> TlvSections = new HashMap<String, TlvSection>(); 
	
	public final static class TlvSection {
		@Byte
		private int SMP_DATA_TYPE = SmpDataTypeDef.get(DATA_TYPE_ENUM.DT_STRING);
		@Byte(2)
		private int SMP_DATA_LEN = 0;
		//@Byte(-1)
		//private byte[] SMP_DATA_VAL = null;
		@Byte(-1)
		private Object SMP_DATA_VAL = null;//保留value的指针，在需要的地方再统一构造tlv，复制字段，以防中间多次拷贝浪费时间；
		TlvSection(DATA_TYPE_ENUM dataType, Object value){
			this.SMP_DATA_TYPE = SmpDataTypeDef.get(dataType);
			this.SMP_DATA_VAL = value;
			}
	}
	
	public Smp(HashMap<String, TlvSection> tlvSections){
		this.resetTlvSections(tlvSections);
	}

	private void resetSortTlvSections(HashMap<String, TlvSection> tlvSections) {

		resetTlvSections(tlvSections);
		SortTlvSections();
    }
	
	private void resetTlvSections(HashMap<String, TlvSection> tlvSections) {
    	//清空原来TlvSections
		TlvSections.clear();
		//Reset tlv sections
		Iterator<Entry<String, TlvSection>> iter = tlvSections.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, TlvSection> entry = (Map.Entry<String, TlvSection>) iter.next();
			TlvSections.put(entry.getKey(), entry.getValue());
		}
    }
	
	private void SortTlvSections(){
		//Sort tlv sections (optional step!)
		List<Map.Entry<String, TlvSection>> TlvSectionsList = 
				new ArrayList<Map.Entry<String, TlvSection>>(TlvSections.entrySet());
    	Collections.sort(TlvSectionsList, new Comparator<Map.Entry<String, TlvSection>>() {
    		public int compare(Map.Entry<String, TlvSection> o1, Map.Entry<String, TlvSection> o2) {
    			// 根据value值排序
    			// return (o2.getValue() - o1.getValue());
    			// 根据key值排序
    			return (o1.getKey()).toString().compareTo(o2.getKey());	}
    	});
	}

	public int getSMP_HEAD_LEN() {
		return SMP_HEAD_LEN;
	}

	public void setSMP_HEAD_LEN(int sMP_HEAD_LEN) {
		SMP_HEAD_LEN = sMP_HEAD_LEN;
	}

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

	public int getSMP_HEAD_CHKSUM() {
		return SMP_HEAD_CHKSUM;
	}

	public void setSMP_HEAD_CHKSUM(int sMP_HEAD_CHKSUM) {
		SMP_HEAD_CHKSUM = sMP_HEAD_CHKSUM;
	}

	public int getSMP_DATA_TOTAL() {
		return SMP_DATA_TOTAL;
	}

	public void setSMP_DATA_TOTAL(int sMP_DATA_TOTAL) {
		SMP_DATA_TOTAL = sMP_DATA_TOTAL;
	}

	public static int getSmpFlag() {
		return SMP_FLAG;
	}

	public static int getSmpVer() {
		return SMP_VER;
	}

	public static byte[] getSmpHeadReserved() {
		return SMP_HEAD_RESERVED;
	}

	public static int getSmpBodyFlag() {
		return SMP_BODY_FLAG;
	}

	public static byte[] getSmpBodyReserved() {
		return SMP_BODY_RESERVED;
	}

	public static HashMap<DATA_TYPE_ENUM, Integer> getSmpdatatypedef() {
		return SmpDataTypeDef;
	}

	public HashMap<String, TlvSection> getTlvSections() {
		return TlvSections;
	}


}
