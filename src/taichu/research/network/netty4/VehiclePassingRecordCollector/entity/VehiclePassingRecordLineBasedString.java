/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.entity;

import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public class VehiclePassingRecordLineBasedString extends VehiclePassingRecord {

	// /*
	// 一条典型的消息记录举例如下(双引号内)：
	// Record example = “section1|section2|...|sectionX|...|sectionN”
	// Record definition:
	// 1.消息以字符串形式，“utf-8”编码，明文非加密非压缩的通过socket的TCP模式从client传到server端；
	// 2.消息以行为单位，行之间的分隔符以“\r”，“\r\n”，“\n”三选一来区分一行；
	// server解析的时候应该支持三种分隔符（三种分隔符是为了兼容可能来自3种平台（WIN/LINUX/MAC）的client;
	// 3.在一条消息记录内，如上example，是以竖杠“|”为分割，头尾无竖杠，中间是字符串section的payload；
	// 4.section可以是典型的“key=value”，或者诸如“String1,string2,string3”等逗号分割接口，可自定协商；
	// 5.特别注意：一条record内不允许有行分界符（“\r”，“\r\n”，“\n”）；
	// 6.特别注意：每个section内不允许有分界符（竖杠“|”）； 7.允许支持中文（半角）和英文，且必须为“utf-8”
	// */

	
	
	//Detail definition of Record
	//max length of ONE line (TCP/SOCKET MTU一般是1452，我们取小于此最大传输单元）
	//ref (http://blog.csdn.net/mazongqiang/article/details/8171805)
	//其实可能netty自己管控这网卡硬件或OS的buffer大小，详细待查 TODO：
	public static final int MSG_LINE_MAX_LENGTH = 1200;
	
	//socket mode
	public static final boolean SOCKET_MODE_IS_TCP_NOT_UDP=true;
	//end of line
	public static final String EOL=Delimiters.EOL_LFCR;
	//end of section
	public static final String EOS=Delimiters.Delimiter_verticalbar;
	//idle/sleep time as gap between TWO Records
	public static final int SLEEP_MS=0;
	//charset
	public static final String CHARSET="UTF_8";
		
	public static boolean lineLengthBeyondLimitation(String record) {
		return record.length()>MSG_LINE_MAX_LENGTH;
	}
	
	public static String genOneMessage(VehiclePassingRecord record){
		return T.getT().reflect.OutputEntityFieldsAsCsvLine(record);
	}


}
