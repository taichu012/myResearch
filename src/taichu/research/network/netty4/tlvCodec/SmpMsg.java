package taichu.research.network.netty4.tlvCodec;
import java.util.HashMap;
import java.util.Random;
import taichu.research.network.netty4.memcachedCodec.Opcode;
import taichu.research.network.netty4.tlvCodec.annotation.Byte;

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
public class SmpMsg {
	
	/*****************************************
	 *********SMP HEADER DEFINE***************
	 ******************************************/
	
	//java的byte是-127到128，无法表达0x88（10001000），为程序方便将0x88存放到int的4字节最低字节，
	//当写入网络字节流的时候使用函数“writeByte(int)"则会将4字节int的低8位作为1个byte写入网络，并字节增1，
	//***详见annotation“Byte”的定义及说明；
	@Byte //1字节为默认可不写出
	public static final int SMP_FLAG = 0x88;//不允许更改，magic number
	@Byte
	private static final int SMP_VER = 0x10;//占1字节，用int4字节容器装值，writeByte写网络1字节
	@Byte(2)
	private final int SMP_HEAD_LEN = 0; //被动计算值
	@Byte(4)
	private final int SMP_MSG_ID = (int)System.nanoTime(); //TODO：考虑怎么使用随机数
	@Byte(4)
	private final int SMP_BODY_LEN = 0; //被动计算值；占2字节，用int4字节容器装值，writeShort写网络2字节
	@Byte(3)
	private static final byte[] SMP_HEAD_RESERVED = new byte[]{0x00,0x00,0x00};
	@Byte
	private final int SMP_HEAD_CHKSUM = 0x00;//被动计算值；占4字节，用int4字节容器装值，writeInt写网络4字节
	
	/*****************************************
	 *********SMP BODY DEFINE***************
	 ******************************************/
	
	@Byte
	public static final int SMP_BODY_FLAG = 0x87;//不允许更改，magic number
	@Byte
	public int SMP_DATA_TOTAL = 0;
	@Byte(2)
	private static final byte[] SMP_BODY_RESERVED = new byte[]{0x00,0x00,0x00};
	
	//根据TLVCODEC约定，动态的传出不同的TLV section，也能根据协议自动编解码！
	//但实际使用中，最好将协议及其带着几个TLV section的定义确定，而非动态变化和计算；
	//如下是静态定义，根据协议定义了2个TLV小结;则此协议在runtime不支持动态变更TLV协议；
	HashMap<String, TlvSection> hashMap = new HashMap<String, TlvSection>(){
		{put("tlvsec1_String", new TlvSection(0x01));
		 put("tlvsec2_Bytes", new TlvSection(0x02));}
	}; 
	
	public final static class TlvSection{
		
		public static class DataType{
			public static final int STRING = 0x01;
			public static final int BYTES = 0x02;
		}
		@Byte
		private int SMP_DATA_TYPE = DataType.STRING;
		@Byte(2)
		private int SMP_DATA_LEN = 0;
		@Byte(-1)
		private byte[] SMP_DATA_VAL = null;
		public TlvSection(int dataType){
			this.SMP_DATA_TYPE=dataType;
		}
	}
	
	
	
    private static final Random rand = new Random();
    private final int flags = 0xdeadbeef; //=4bytes/8个char
    private final int id = rand.nextInt(); //Opaque

    //根据协议，虽然可支持根据TLV section每次自动塞入，但暂时不支持
    private SmpMsg(HashMap<String, TlvSection> tlvmap) { /* 暂不支持这个构造*/    }

    public SmpMsg(String string, byte[] bytes) {
        initTlvSec(TlvSection.DataType.STRING, string);
        initTlvSec(TlvSection.DataType.BYTES, bytes);
    }
    
    private static void initTlvSec(int DataType, Object value){
    	//TODO:根据DataType来转换OBJ并初始化tlv section
    }


}
