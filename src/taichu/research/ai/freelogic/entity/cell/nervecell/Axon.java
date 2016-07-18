/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.relation.LinkAxon2Dendrite;

/**
 * @author ya
 * ��ͻ��һ����Ԫ����һ����ͻ������Ҫ�������źŸ�������Ԫ
 */
public class Axon {

	//Ψһ����ͻ�Ӵ��ı�����Ԫ����ͻ��һ���������ͻ��
	//TODO �붨��һ�����ӵ���ͻ�����飨ʸ����hashmap����������<��Ԫ����ͻʵ��> ��һ����ͻ�����������ĸ���Ԫ����Ϣ����
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
	
	//���ӵ�һ����ͻ�ϡ�
	public LinkAxon2Dendrite ConnectTo(Dendrite dendrite){
		//TODO
		
		return new LinkAxon2Dendrite(LinkAxon2Dendrite.AXON_2_DENDRITE, this, dendrite);
	}
	
	//TODO ��ͻҲ����������Ҫ����Ȼ�ﵽ��Ԫ���񾭳嶯����ֵ�������źŴ��ݸ�������Ԫ��ǿ������ͻ������



	//TODO ������ͻ����ͻ��ǿ��
	public void AdjustLinkPower(LinkAxon2Dendrite link, long delta){
		link.setLinkPower(delta);
	}

	
}
