package taichu.research.network.netty4.VehiclePassingRecordCollector;

import taichu.research.tool.T;

public class Conf {
	private static final String INIFILE="D:/SourceRemote/GIT/MyResearch/"
			+"src/taichu/research/network/netty4/VehiclePassingRecordCollector/config.ini";
	
	private static final String INIFILE_TAIL="/taichu/research/network/netty4/VehiclePassingRecordCollector/config.ini";
	
	public static String getIniPath() {return INIFILE;}
	
	private static String getIniPathBasedOnClassPath() {
		String str = T.getT().ossys.getRealPath(Conf.class);
		return str.replace('\\', '/') + INIFILE_TAIL;
	}
	
	public static String getIniPathRelative(){
		return getIniPathBasedOnClassPath();
	}
	
	public static void main(String[] args) {
		System.out.println(Conf.getIniPath());
		System.out.println(Conf.getIniPathRelative());
		
		
	}
	
}
