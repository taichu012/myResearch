/**
 * 
 */
package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface IVehicleTraffiecRecordDelimiterBasedString {


	// 本协议接口支持“<$>”作为分隔符
	@SuppressWarnings("serial")
	public static final Map<String, byte[]> DelimiterAllowedBytes = new HashMap<String, byte[]>() {
		{
			put("allos_<$>", new byte[] { '<', '$','>' });
		}
	};
	
	// 本协议接口支持“<$>”作为分隔符
	public static final String DelimiterAllowedString = "<$>";

}
