package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

public interface IVehicleTrafficRecord {
	// 根据record定义，最长行不超过500个字符（char或byte）
	public static final int MSG_LINE_MAX_LENGTH = 250;
	
	//TODO:其他格式定义（公共的）请在这个父接口说明
	
	public static final boolean SOCKET_TCP=true;
	
	public static final boolean SOCKET_UDP=false;
}
