package taichu.research.network.netty4.tlvCodec;
import java.util.HashMap;
import java.util.Random;
import taichu.research.network.netty4.memcachedCodec.Opcode;
import taichu.research.network.netty4.tlvCodec.annotation.Byte;
import taichu.research.network.netty4.tlvCodec.annotation.Byte.DataContainer;
import taichu.research.network.netty4.tlvCodec.annotation.Byte.DataDef;
import taichu.research.network.netty4.tlvCodec.annotation.Byte.DataWriter;

public class SmpMsg {
	
	/*****************************************
	 *********SMP HEADER DEFINE***************
	 ******************************************/
	
	//java的byte是-127到128，无法表达0x88（10001000），但为程序方便将0x88存放到int的4字节最低字节，
	//当写入网络字节流的时候使用函数“writeByte(int)"则会将4个字节的int的低8位作为1个byte写入网络，并字节增1，
	//则等于将int（00,00,00,88）四个字节的最低1个字节0x88写到字节流，达到了我们的要求，int类型只是一个容器.
	//***详见annotation的定义，它们用于自动TLV编解码；
	
	//所有固定值都是static静态；需改动后生成的不需要static
	
	@Byte //annotation的3个参数将最常用的值定义为default默认值，所以不用写出来了。
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
	HashMap<String, TlvSection> hashMap = new HashMap<String, TlvSection>(){
		{put("tlvForString", new TlvSection(0x01));
		 put("tlvForBytes", new TlvSection(0x02));}
	}; 
	
	public final static class TlvSection{
		@Byte
		private int SMP_DATA_TYPE = 0x01;
		@Byte(2)
		private int SMP_DATA_LEN = 0;
		@Byte(-1)
		private byte[] SMP_DATA_VAL = null;
		public TlvSection(int dataType){
			this.SMP_DATA_TYPE=dataType;
		}
	}
	
	
	
    private static final Random rand = new Random();
    private final int flags = 0xdeadbeef; //random
    private final int id = rand.nextInt(); //Opaque

    //TODO：
    public SmpMsg(byte opcode, String key, String value) {
        this.opCode = opcode;
        this.key = key;
        this.body = value == null ? "" : value;
        this.expires = 0;
        //only set command has extras in our example
        hasExtras = opcode == Opcode.SET;
    }

    public SmpMsg(byte opCode, String key) {
        this(opCode, key, null);
    }

    public int magic() { //2
        return magic;
    }

    public int opCode() {  //3
        return opCode;
    }

    public String key() {  //4
        return key;
    }

    public int flags() {  //5
        return flags;
    }

    public int expires() {  //6
        return expires;
    }

    public String body() {  //7
        return body;
    }

    public int id() {  //8
        return id;
    }

    public long cas() {  //9
        return cas;
    }

    public boolean hasExtras() {  //10
        return hasExtras;
    }

}
