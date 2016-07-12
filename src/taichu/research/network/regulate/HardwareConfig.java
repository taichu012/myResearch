/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taihcu
 *
 */
public class HardwareConfig {
	
	//TODO：返回工作节点的环境硬件配置（CPU/MEM/DISK/NETWORK）
	
	//cpu的core数目。举例：3就等于3个core（一个超线程的逻辑core也算一个core）；
	private int cpuCoreNbr=0;
	
	//cpu的主频。举例：2.3就等于2.3GHZ主频；
	private float cpuGhz=0.0f;
	
	//mem的大小。举例：1500就等于1.5GBytes
	private int memMb=0;
	
	//disk的大小。举例：2300就等于2.3GBytes；
	private int diskMb=0;
	
	//network的大小。举例：17等于17mbits
	private int networkMb=0;
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {


	}

}
