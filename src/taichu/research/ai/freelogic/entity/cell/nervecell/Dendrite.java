/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.relation.LinkAxon2Dendrite;

/**
 * @author ya
 * 树突，一个神经元有多个树突，它为神经元接受信号，它有权重（信号在神经元中所属的权重地位）
 */
public class Dendrite {

	
	//TODO 请定义一个哪些轴突链接本树突的数组（矢量或hashmap），举例：<神经元的轴突实例>
	private LinkAxon2Dendrite[] linkAxon2Dendrite[]=null;
	
	//默认轴突到树突的链接强度！和神经元的冲动阈值无关！
	public static final long PAOWER_2_NERVECELL_DEFAULT = 100L;
	
	private long power2NerveCell = PAOWER_2_NERVECELL_DEFAULT;

	public long getPower2NerveCell() {
		return power2NerveCell;
	}

	public void setPower2NerveCell(long power2NerveCell) {
		this.power2NerveCell = power2NerveCell;
	}

	/**
	 * 
	 */
	public Dendrite() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	//链接到一个树突上。
	public LinkAxon2Dendrite ConnectTo(Axon axon){
		//TODO
		
		return new LinkAxon2Dendrite(LinkAxon2Dendrite.AXON_2_DENDRITE, axon, this);
	}

}
