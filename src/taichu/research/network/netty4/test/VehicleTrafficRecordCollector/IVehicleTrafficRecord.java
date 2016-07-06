package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

public interface IVehicleTrafficRecord {
	// 根据record定义，最长行不超过500个字符（char或byte）
	public static final int MSG_LINE_MAX_LENGTH = 500;
}
