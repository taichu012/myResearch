/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.protocal;

import taichu.research.tool.Delimiters;

/**
 * @author taichu
 *
 */
public interface Smp  {

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
	public static final String HEARTBEAT_CHARS="HB";
	//headbeat pattern（paired message）
	public static final String HEARTBEAT_REQ="PING";
	public static final String HEARTBEAT_RSP="PONG";


}
