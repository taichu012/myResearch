package taichu.research.network.netty4.tlvCodec.protocol.smp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import taichu.research.network.netty4.tlvCodec.core.annotation.Byte;
import taichu.research.network.netty4.tlvCodec.core.annotation.TLV;
import taichu.research.network.netty4.tlvCodec.core.annotation.TLV.IS;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp.Tlv;
import taichu.research.tool.T;

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
	
	//***详见annotation“的定义及说明；
	@Byte//1字节为默认可不写出
	public static final byte SMP_FLAG = 0x66;//不允许更改，协议固定值
	@Byte
	public static final byte SMP_VER = 0x10;//不允许更改，协议固定值
	@Byte(2)
	public static final int SMP_HEAD_LEN = 16;
	//public static final byte[] SMP_HEAD_LEN = T.Type.intToBytes(0x11,2); 不允许更改，协议固定值
	@Byte(4)
	private int SMP_MSG_ID = 0;//按需生成
	@Byte(4)
	private int SMP_BODY_LEN = 0;//按需生成
	@Byte(3)
	public static final byte[] SMP_HEAD_RESERVED = new byte[]{0x00,0x00,0x00};
	@Byte
	private byte SMP_HEAD_CHKSUM = 0x00;//按需生成
	
	/*****************************************
	 *********SMP BODY DEFINE***************
	 ******************************************/
	@Byte
	public static final byte SMP_BODY_FLAG = 0x67;//不允许更改，协议固定值
	@Byte
	private int SMP_DATA_TOTAL = 0;//按需生成
	@Byte(2)
	public static final byte[] SMP_BODY_RESERVED = new byte[]{0x00,0x00};
	@Byte(2)
	private int SMP_BODY_CHKSUM = 0;
	
	//根据SMP协议规定，TLV Section可重复多个；TLVCodec应能动态计算；
	//根据SMP协议，并未定义TLV Section必须有固定次序，所以用HaspMap，可不排序，不保证次序；
	private HashMap<String, Tlv> tlvs = new HashMap<String, Tlv>(); 
	
	public final static class Tlv {
		@Byte
		@TLV(IS.Type)
		public byte SMP_DATA_TYPE = 0;
		@Byte(2)
		@TLV(IS.Length)
		public int SMP_DATA_LEN = 0;
		@Byte(-1)
		@TLV(IS.Value)
		public byte[] SMP_DATA_VAL = null;//按需生成
		
		//构造函数
		Tlv(int type, byte[] value){
			this.SMP_DATA_TYPE = (byte)type;
			this.SMP_DATA_LEN = value.length;
			T.Type.byteArrayCopy(value, this.SMP_DATA_VAL);
			}
	}
	
	//只允许在smp消息实例构造的时候初始化设定tlv字段一次;
	//根据SMP协议，tlv字段的发送是不保证顺序的！
	public Smp(HashMap<String, Tlv> tlvs){
		this.resetAndSortTlvs(tlvs);
		this.SMP_DATA_TOTAL=T.Type.intToBytes(tlvs.size(),1)[0];
	}

	private void resetAndSortTlvs(HashMap<String, Tlv> tlvs) {
		resetTlvs(tlvs);
		SortTlvs();
    }
	
	private void resetTlvs(HashMap<String, Tlv> tlvSections) {
    	//清空原来TlvSections
		tlvs.clear();
		//Reset tlv sections
		Iterator<Entry<String, Tlv>> iter = tlvSections.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Tlv> entry = (Map.Entry<String, Tlv>) iter.next();
			tlvs.put(entry.getKey(), entry.getValue());
		}
    }
	
	//虽然SMP并未定义tlv字段的顺序，但这里按照key的字符串顺序排序；
	//所以初始化smp实例的时候可以按照<"tlv1",tlv>,<"tvl2",tlv>来初始化较好；
	private void SortTlvs(){
		//Sort tlv sections (optional step!)
		List<Map.Entry<String, Tlv>> TlvSectionsList = 
				new ArrayList<Map.Entry<String, Tlv>>(tlvs.entrySet());
    	Collections.sort(TlvSectionsList, new Comparator<Map.Entry<String, Tlv>>() {
    		public int compare(Map.Entry<String, Tlv> o1, Map.Entry<String, Tlv> o2) {
    			// 根据value值排序
    			// return (o2.getValue() - o1.getValue());
    			// 根据key值排序
    			return (o1.getKey()).toString().compareTo(o2.getKey());	}
    	});
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

	public byte getSMP_HEAD_CHKSUM() {
		return SMP_HEAD_CHKSUM;
	}

	public void setSMP_HEAD_CHKSUM(byte sMP_HEAD_CHKSUM) {
		SMP_HEAD_CHKSUM = sMP_HEAD_CHKSUM;
	}

	public int getSMP_DATA_TOTAL() {
		return SMP_DATA_TOTAL;
	}

	public void setSMP_DATA_TOTAL(byte sMP_DATA_TOTAL) {
		SMP_DATA_TOTAL = sMP_DATA_TOTAL;
	}

	public int getSMP_BODY_CHKSUM() {
		return SMP_BODY_CHKSUM;
	}

	public void setSMP_BODY_CHKSUM(int sMP_BODY_CHKSUM) {
		SMP_BODY_CHKSUM = sMP_BODY_CHKSUM;
	}

	public HashMap<String, Tlv> getTlvs() {
		return tlvs;
	}

	public void setTlvs(HashMap<String, Tlv> tlvs) {
		this.tlvs = tlvs;
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

	public static byte getSmpBodyFlag() {
		return SMP_BODY_FLAG;
	}

	public static byte[] getSmpBodyReserved() {
		return SMP_BODY_RESERVED;
	}

	public static void main(String[] args){
		
		//http://blog.163.com/yurong_1987@126/blog/static/47517863200911314245752/
		byte b = (byte)0x88; //byte是1字节，不够放136，则按照补码规则计算得到-120
		char c = 0x88;//char是2字节，够放136，则得到136
		int i = 0x88;//char是2字节，够放136，则得到136
		
		byte b2 = (byte)c;//强制转为1字节后，丢失精度，按照补码规则，依然是-120
		byte b3 = (byte)i;//强制转为1字节后，丢失精度，按照补码规则，依然是-120
		int i2 = b&0xFF;
		
		//结论：按照字面值（136十进制=0x88十六进制），设定到带符号的byte中（-127到128），超过范围；
		//    所以只能强行转换为（byte），就用了补码形式，得到逻辑上-120，但是接收端无法从-120转换回逻辑上的0x88！
		//	      只有一个办法，强转后逻辑上136（0x88）变为1个字节的补码形式的-120，接收端将byte&0xFF后再变为int，就得到原来逻辑的136（等价于0X88）！
		
		System.out.println(b);
		
	}

}
