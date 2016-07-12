/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taichu
 *
 */
public class Node implements IReportStatus, IRegulatable {

	//TODO：所有基础和公用的方法，比如CPU配置，CPU整体load（占核数），CPU当前进程（node）的load（占核数）等；
	//可考虑将node基类变为interface，方便regulator调控者实现代码；
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workloadReduce() {
		// TODO 这里一般需要降低业务量，比如降低发送消息的速度；增加线程sleep；减少工作线程数目等；
		
		
	}

	@Override
	public void workloadIncrease() {
		// TODO 这里一般需要提升业务量，比如提升发送消息的速度；减少线程sleep；增加工作线程数目等；
		
	}

	@Override
	public void setAdjustMode(boolean isLinearNotExponential) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HardwareStatus getHardwareStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HardwareConfig getHardwareConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyStatus getSelfStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
