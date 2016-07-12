/**
 * 
 */
package taichu.research.network.regulate;

import java.util.HashMap;

/**
 * @author taichu
 *
 */
public interface IRegulator {
	
	//实现调控协调者（总控者），类似大脑对身体个部件的调控（管控）；
	//这个接口只是一个规划，并非限定实现类一定如此，只是给出建议；
	
	
	//TODO：
	//1.需要实现一个“impactchain”的map结构，并维护各node的调控因子数据；
	//2.实时按需/周期了解各注册node的状态，维护node状态变化的事件event及触发的相关node的调整工作；
	
	
	//设定A对B的影响（调控）因子，
	//比如ImpactAB=10，则A变动（上升下降K%或K单位），则B变动同方向的（10K%，或10K单位）；
	//假设影响链（impact chain）是A->B->C，但也可设定setImpactPair(a,c)，直接在头尾建立调控关系；
	//按需绑定“专门”事件来根据node的状态变化，自动调节相关node的状态；
	public abstract void setImpactPair(Node a, Node b);

	//检查整条的影响链中的每个元素的状态情况；
	public abstract void CheckImpactChain(HashMap<String,Node> nodeList);
	
	public abstract void startImpactChain(HashMap<String,Node> nodeList);
	public abstract void shutdownImpactChain(HashMap<String,Node> nodeList);
	public abstract void restartImpactChain(HashMap<String,Node> nodeList);
	
	//对node进行不同模式mode的调控操作；
	//mode可以设定为一个option类，方便选择而不会弄错；
	public abstract void regulateNode(Node a, String mode);
	
	//接受node的注册和注销；
	public abstract void egister(Node a);
	public abstract void unregister(Node a);
	
	
	

}
