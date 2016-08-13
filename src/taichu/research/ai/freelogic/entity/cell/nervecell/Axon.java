/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.relation.LinkAxon2Dendrite;

/**
 * @author ya
 * 轴突，一个神经元仅有一个轴突，它主要负责传送信号给比邻神经元
 */
public class Axon {

	//唯一被轴突接触的比邻神经元的树突（一条或多条树突）
	//TODO 请定义一个链接到树突的数组（矢量或hashmap），举例：<神经元的树突实例> （一个树突包含它属于哪个神经元的信息！）
	private LinkAxon2Dendrite[] linkAxon2Dendrite[]=null;

	/**
	 * 
	 */
	public Axon() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	//链接到一个树突上。
	public LinkAxon2Dendrite ConnectTo(Dendrite dendrite){
		//TODO
		
		return new LinkAxon2Dendrite(LinkAxon2Dendrite.AXON_2_DENDRITE, this, dendrite);
	}
	
	//TODO 轴突也有生长的需要，虽然达到神经元的神经冲动的阈值，但是信号传递给下游神经元的强弱有轴突决定！



	//TODO 调节轴突到树突的强度
	public void AdjustLinkPower(LinkAxon2Dendrite link, long delta){
		link.setLinkPower(delta);
	}

	
}
