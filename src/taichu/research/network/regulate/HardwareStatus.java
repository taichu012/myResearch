/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taichu
 *
 */
public class HardwareStatus {

	//TODO：返回工作节点的环境硬件的（全局/整体）状态（CPU/MEM/DISK/NETWORK）
	
	//cpu的占用耗时。举例：2.5就等于占用2.5个核的100%的处理时；
	private float cpuCoreCost=0.0f;
	
	//mem的占用。举例：1500就等于1.5GBytes
	private int memMbCost=0;
	
	//disk的占用。指大量数据结果对disk的占用。举例：2300就等于2.3GBytes；
	private int diskMbCost=0;
	
	//network的占用。举例：17等于17mbits
	private int networkMbCost=0;
	

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
