package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

// “\r”是换行符，“\n”是回车符，对win、linux、mac os苹果三个OS都不同；
// WIN用“\r\n”，linux用“\n”，MAC OS用“\r”，分别都作为文本行结束标记
//详见：http://m.blog.csdn.net/article/details?id=8121951；
// 经查看netty库的源码，class“LineBasedFrameDecoder”只支持win/linux
// os两种，不支持mac os的“\r”作为分隔符，
// 所以本协议接口如果使用LineBasedFrameDecoder作为handler，则
// 必须定义只使用“\r\n”或“\r”，二选一作为行结束标记！不支持\r！


public class Delimiters {

	public static final String EOL_LF="\r";
	public static final String EOL_CR="\n";
	public static final String EOL_LFCR="\r\n";
	
	public static final String LINUX_EOL=EOL_CR;
	public static final String MAC_EOL=EOL_LF;
	public static final String WIN_EOL=EOL_LFCR;
	
	//HINT:java不允许只有返回值不同（函数名和参数表都一样）的成员函数存在！
	public static final String getLineDelimiterStrForWin() {
		return WIN_EOL;
	}
	
	public static final String getLineDelimiterStrForMac() {
		return MAC_EOL;
	}
	
	public static final String getLineDelimiterStrForLinux() {
		return LINUX_EOL;
	}
	
	public static final byte[] getLineDelimiterBytesForWin() {
		return WIN_EOL.getBytes();
	}
	
	public static final byte[] getLineDelimiterBytesForMac() {
		return MAC_EOL.getBytes();
	}
	
	public static final byte[] getLineDelimiterBytesForLinux() {
		return LINUX_EOL.getBytes();
	}
	
	//定义间隔符-dollar-"$"
	public static final String Delimiter_dallar = "$";
	public static final String getDelimiterDollarStr(){return Delimiter_dallar;}
	public static final byte[] getDelimiterDollarBytes(){return Delimiter_dallar.getBytes();}
	
	//定义间隔符-verticalbar-"|"
	public static final String Delimiter_verticalbar = "|";
	public static final String getDelimiterVerticalbarStr(){return Delimiter_verticalbar;}
	public static final byte[] getDelimiterVerticalbarBytes(){return Delimiter_verticalbar.getBytes();}
	
	
}
