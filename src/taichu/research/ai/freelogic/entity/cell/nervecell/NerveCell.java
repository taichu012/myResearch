/**
 * 
 */
package taichu.research.ai.freelogic.entity.cell.nervecell;

import taichu.research.ai.freelogic.entity.cell.Cell;

/**
 * @author ya
 * ��Ԫ��һ�㺬��һ����ͻ��������ͻ����Ȩ�أ���һ���嶯��ֵ����һЩ��ر仯�����ĳ�Ա������
 *
 */
public class NerveCell extends Cell {
	
	//��Ԫ��һ����ͻ
	private Axon axon;
	//TODO����Ԫ��������ͻ��������������ṹ������
	private Dendrite[] dendrite[]=null;
	
	private static final long DEFAULT_IMPULSE_THRESHOLD = 100L;
	private long ImpulseThreshold =  DEFAULT_IMPULSE_THRESHOLD;
	

	//������Ԫ�ĳ嶯��ֵ����һ������delta�������ɸ���������ֱ�����ã���SETXXX���á�
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
	
	//TODO ���ݱ���Ԫ����ͻ������ȷ���Ƿ�ἤ���񾭳嶯��������ͻ���ݸ��²���Ԫ��
	public void TryImpulse(){
	
		//TODO ׼��������ͻ����ͻ��powerȨ�أ���������ֵ���ж��Ƿ񼤷��嶯��
		//������������źŴ��ݵ��²㣬�²㼶������TryImpulse���Ƿ��жϱ���Ԫ�����ġ�
		
	}
	
	
	

}
