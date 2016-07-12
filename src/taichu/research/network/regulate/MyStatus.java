/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taichu
 *
 */
public class MyStatus {
	
	//TODO:参考《Linux下java获取CPU、内存、磁盘IO、网络带宽使用率 》
	//（http://blog.csdn.net/blue_jjw/article/details/8741000）
	//《java 如何获得一个进程的内存使用情况，cpu运行的时间》
	//（http://zhidao.baidu.com/link?url=hubHQZsN948NodvFVjUPCTiPjDdtsSx66s3oEO4GiVNsGdc_Yzk9o8bDhij76H1PtV4hPiEtnz_6aSryOWmELq）
	
	//cpu的占用耗时。举例：2.5就等于占用2.5个核的100%的处理时；
	private float cpuCoreCost=0.0f;
	
	//mem的占用。举例：1500就等于1.5GBytes
	private int memMbCost=0;
	
	//disk的占用。指大量数据结果对disk的占用。举例：2300就等于2.3GBytes；
	private int diskMbCost=0;
	
	//network的占用。举例：17等于17mbits
	private int networkMbCost=0;
	
	//逻辑上（和处理业务速度线性相关的估计）的业务量。举例：70指处于满业务量的70%；
	private int workloadCost=0;
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	

}
