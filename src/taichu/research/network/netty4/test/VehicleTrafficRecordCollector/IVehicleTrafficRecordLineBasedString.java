/**
 * 
 */
package taichu.research.network.netty4.test.VehicleTrafficRecordCollector;

/**
 * @author taichu
 *
 */
public interface IVehicleTrafficRecordLineBasedString extends IVehicleTrafficRecord {

	/*
	 一条典型的消息记录举例如下(双引号内)：
	 Record example = “section1|section2|...|sectionX|...|sectionN”
	 Record definition:
	     1.消息以字符串形式，“utf-8”编码，明文非加密非压缩的通过socket的TCP模式从client传到server端；
	     2.消息以行为单位，行之间的分隔符以“\r”，“\r\n”，“\n”三选一来区分一行；
	        server解析的时候应该支持三种分隔符（三种分隔符是为了兼容可能来自3种平台（WIN/LINUX/MAC）的client;
	     3.在一条消息记录内，如上example，是以竖杠“|”为分割，头尾无竖杠，中间是字符串section的payload；
	     4.section可以是典型的“key=value”，或者诸如“String1,string2,string3”等逗号分割接口，可自定协商；
	     5.特别注意：一条record内不允许有行分界符（“\r”，“\r\n”，“\n”）；
	     6.特别注意：每个section内不允许有分界符（竖杠“|”）；
	     7.允许支持中文（半角）和英文，且必须为“utf-8”
	 
	 */
	

}
