/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taichu
 * 这个接口要求实现者有能力上报自己的状态
 *
 */
public interface IReportStatus {
	
	//report hardware(CPU/MEM/DISK/NETWORD...) status
	public abstract HardwareStatus getHardwareStatus();
	
	//report hardware configuration
	public abstract HardwareConfig getHardwareConfig();
	
	//report self status
	public abstract MyStatus getSelfStatus();

}
