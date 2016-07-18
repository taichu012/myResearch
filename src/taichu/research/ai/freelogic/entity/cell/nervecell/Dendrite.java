/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.relation.LinkAxon2Dendrite;

/**
 * @author ya
 * ��ͻ��һ����Ԫ�ж����ͻ����Ϊ��Ԫ�����źţ�����Ȩ�أ��ź�����Ԫ��������Ȩ�ص�λ��
 */
public class Dendrite {

	
	//TODO �붨��һ����Щ��ͻ���ӱ���ͻ�����飨ʸ����hashmap����������<��Ԫ����ͻʵ��>
	private LinkAxon2Dendrite[] linkAxon2Dendrite[]=null;
	
	//Ĭ����ͻ����ͻ������ǿ�ȣ�����Ԫ�ĳ嶯��ֵ�޹أ�
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
	
	//���ӵ�һ����ͻ�ϡ�
	public LinkAxon2Dendrite ConnectTo(Axon axon){
		//TODO
		
		return new LinkAxon2Dendrite(LinkAxon2Dendrite.AXON_2_DENDRITE, axon, this);
	}

}
