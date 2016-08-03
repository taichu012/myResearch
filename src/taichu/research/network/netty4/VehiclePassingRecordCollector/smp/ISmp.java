/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.smp;

import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public interface ISmp  {

	// /*
	// 协议说明详见文档《SMP协议说明.txt》
	// */

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
	//headbeat pattern（one message）
	public static final String HEARTBEAT_HB="HB";
	//headbeat pattern（paired message）
	public static final String HEARTBEAT_PING="PING";
	public static final String HEARTBEAT_PONG="PONG";
	//timeout config
	public static final long READ_IDEL_TIMEOUT_S = 15; // 读超时
	public static final long WRITE_IDEL_TIMEOUT_S = 20;// 写超时
	public static final long ALL_IDEL_TIMEOUT_S = 30; // 所有超时
	
	
	
	public static String genLineFromMsg(VehiclePassingRecord msg){
		return T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(msg);
	}
	
	public static String gen32BytesMd5(String data) throws Exception {
		return T.Security.genMd5WithBytes32(data);
	}
	
	public static String gen8BytesMd5(String data) throws Exception {
		return T.Security.genMd5WithBytes8(data);
	}
	

	public static boolean isHeartBeat(String msg) {
    	if (Smp.HEARTBEAT_HB.equals(msg)||
    			Smp.HEARTBEAT_PING.equals(msg)||
    			Smp.HEARTBEAT_PONG.equals(msg)){
    		return true;
    	}else {
    		return false;
    	}
	}
	
	//Detail definition of Record
	//max length of ONE message (TCP/SOCKET MTU一般是1452，我们取小于此最大传输单元）
	//ref (http://blog.csdn.net/mazongqiang/article/details/8171805)
	//其实可能netty自己管控这网卡硬件或OS的buffer大小，上层逻辑一般不处理；
	//TODO：为了提升效率，可进一步针对MTU来做优化；
	public static final int MSG_LINE_MAX_LENGTH = 1200;
	
	public static boolean msgTooLong(String msg) {
		return msg.length()>MSG_LINE_MAX_LENGTH;
	}
	
}
