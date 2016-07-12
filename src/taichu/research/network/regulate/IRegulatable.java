/**
 * 
 */
package taichu.research.network.regulate;

/**
 * @author taichu
 *
 */
public interface IRegulatable {

	//接受调控的业务实体必须实现本接口
	
	public abstract void shutdown();
	public abstract void restart();
	public abstract void start();
	public abstract void workloadReduce();
	public abstract void workloadIncrease();
	//设定调控速度的变化模式：线性变化或指数级变化，由实现层自己定义，管控层逻辑调用。
	public abstract void setAdjustMode(boolean isLinearNotExponential);
}
