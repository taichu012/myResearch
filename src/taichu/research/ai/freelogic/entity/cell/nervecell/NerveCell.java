/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.entity.cell.Cell;

/**
 * @author ya
 * 神经元，一般含有一个轴突，若干树突（含权重），一个冲动阈值，及一些相关变化动作的成员函数。
 *
 */
public class NerveCell extends Cell {
	
	//神经元有一个轴突
	private Axon axon;
	//TODO：神经元有若干树突，用数组或其他结构描述。
	private Dendrite[] dendrite[]=null;
	
	private static final long DEFAULT_IMPULSE_THRESHOLD = 100L;
	private long ImpulseThreshold =  DEFAULT_IMPULSE_THRESHOLD;
	

	//调整神经元的冲动阈值，用一个增量delta（可正可负），方便直观设置，比SETXXX好用。
	public void AdjustImpulseThreshold(long delta){
		this.ImpulseThreshold += delta;
	}
	
	public long getImpulseThreshold() {
		return ImpulseThreshold;
	}

	public void setImpulseThreshold(long impulseThreshold) {
		ImpulseThreshold = impulseThreshold;
	}

	/**
	 * 
	 */
	public NerveCell() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	//TODO 根据本神经元的树突输入来确定是否会激发神经冲动，经由轴突传递给下层神经元？
	public void TryImpulse(){
	
		//TODO 准备数据树突及树突的power权重，并按照阈值来判断是否激发冲动。
		//如果激发，则信号传递到下层，下层级联的做TryImpulse，是否中断本神经元不关心。
		
	}
	
	
	

}
