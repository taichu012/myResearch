/**
 * 
 */
package taichu.research.ai.freelogic.relation;

import taichu.research.ai.freelogic.entity.cell.nervecell.Axon;
import taichu.research.ai.freelogic.entity.cell.nervecell.Dendrite;


/**
 * @author ya
 * ����ָ��ͻ����ͻ�����ӣ�Ҳ��ָ�����źŴ��ݵķ���Ĭ����ͻ����ͻ��Ҳ����ͻ����ͻ
 */
public class LinkAxon2Dendrite {
	
	//�źŷ�����ͻ����ͻ��Ĭ�ϡ�
	public static final boolean AXON_2_DENDRITE = true;
	//�źŷ�����ͻ����ͻ�����٣�
	public static final boolean DENDRITE_2_AXON = true;
	
	private boolean impulseDirection = AXON_2_DENDRITE;
	
	//Ĭ����ͻ����ͻ������ǿ�ȣ�����Ԫ�ĳ嶯��ֵ�޹أ�
	public static final long LINK_PAOWER_DEFAULT = 100L;
	
	private Axon axon =null;
	private Dendrite dendrite=null;
	private long linkPower =LINK_PAOWER_DEFAULT;
	
	

	public long getLinkPower() {
		return linkPower;
	}

	public void setLinkPower(long linkPower) {
		this.linkPower = linkPower;
	}

	public Axon getAxon() {
		return axon;
	}

	public void setAxon(Axon axon) {
		this.axon = axon;
	}

	public Dendrite getDendrite() {
		return dendrite;
	}

	public void setDendrite(Dendrite dendrite) {
		this.dendrite = dendrite;
	}

	/**
	 * 
	 */
	public LinkAxon2Dendrite(boolean ImpulseDirection) {
		this.impulseDirection=ImpulseDirection;
	}
	
	/**
	 * 
	 */
	public LinkAxon2Dendrite() {
		this.impulseDirection=AXON_2_DENDRITE;
	}
	
	public LinkAxon2Dendrite(boolean impulseDirection, Axon axon,
			Dendrite dendrite) {
		super();
		this.impulseDirection = impulseDirection;
		this.axon = axon;
		this.dendrite = dendrite;
	}
	




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
