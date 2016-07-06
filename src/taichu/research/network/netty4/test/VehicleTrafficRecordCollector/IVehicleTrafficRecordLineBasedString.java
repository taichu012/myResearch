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

	//TODO:其他格式定义（子类自己的）请在这个子接口说明
	//TODO：请改为内部类来更好的调用\r,\n,\r\n；
	//TODO：并测试添加多个行分隔符的frame切分；

	// “\r”是换行符，“\n”是回车符，对win、linux、mac os苹果三个OS都不同；
	// WIN用“\r\n”，linux用“\n”，MAC OS用“\r”，分别都作为文本行结束标记；
	// 经查看netty库的源码，class“LineBasedFrameDecoder”只支持win/linux
	// os两种，不支持mac os的“\r”作为分隔符，
	// 所以本协议接口支持支“\r\n”或“\r”，二选一的行结束标记！切记！
	@SuppressWarnings("serial")
	public static final Map<String, byte[]> DelimiterBytesAllowed = new HashMap<String, byte[]>() {
		{
			put("win_r_n", new byte[] { '\r', '\n' });
			put("linux_n", new byte[] { (byte) '\n' });
		}
	};

	public static final Map<String, byte[]> DelimiterBytesNowAllowed = new HashMap<String, byte[]>() {
		{
			put("mac_r", new byte[] { (byte) '\r' });
		}
	};

	public static final Map<String, String> DelimiterStringAllowed = new HashMap<String, String>() {
		{
			put("win_r_n","\r\n");
			put("linux_n", "\n");
		}
	};

	public static final Map<String, String> DelimiterStringNowAllowed = new HashMap<String, String>(){
		{ put("mac_r", "\r");
		};
	};

}
