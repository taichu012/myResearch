/**
 * 
 */
package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;

/**
 * @author Administrator
 *
 */
public interface IVehicleTrafficRecordLineBasedString extends IVehicleTrafficRecord {


	// “\r”是换行符，“\n”是回车符，对win、linux、mac os苹果三个OS都不同；
	// WIN用“\r\n”，linux用“\r”，MAC OS用“\n”，分别都作为文本行结束标记；
	// 经查看netty库的源码，class“LineBasedFrameDecoder”只支持win/mac
	// os两种，不支持linux的“\r”作为分隔符，
	// 所以本协议接口支持支“\r\n”或“\r”，二选一的行结束标记！切记！
	@SuppressWarnings("serial")
	public static final Map<String, byte[]> DelimiterAllowed = new HashMap<String, byte[]>() {
		{
			put("winos_r_n", new byte[] { '\r', '\n' });
			put("macos_n", new byte[] { (byte) '\n' });
		}
	};

	public static final Map<String, byte[]> DelimiterNowAllowed = new HashMap<String, byte[]>() {
		{
			put("linuxos_r", new byte[] { (byte) '\r' });
		}
	};

}
