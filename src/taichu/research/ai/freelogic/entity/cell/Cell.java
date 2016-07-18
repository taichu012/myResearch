package taichu.research.ai.freelogic.entity.cell;



public class Cell implements Cloneable {
	
	//TODO ϸ������
	private void Die(){
		
	}
	
	//TODO �½�ϸ��
	public Cell(){
	}
	
	//ϸ���Է��ķ���
	private Cell DoDivision(Cell old){
		
		//TODO ��ʱͨ��clone������Ϊ��ϸ������
		//һ���Ϊ2�֣���1��java�﷨����Ҫ��clone����2���߼�ҵ���ϵġ�ϸ�����ѡ��������ʵ����Ҫ�޸ģ�
		return (Cell) old.clone();
	}
	
	//����ʵ����Ҫ����Cell�����Ա���ݶ��޸ģ�
	//TODO
	public Object clone() {   
		Cell o = null;   
        try {   
            o = (Cell) super.clone();   
            //��clone���е���
            //������Ҫ��Cell�ڲ���Ԫ�غ����ݶ�clone������
//            for(int i=0;i<timePairStack.size();i++){  
//            	TimePair tp=(TimePair) timePairStack.get(i).clone();//��ȻClass BҲҪʵ����Ӧclone����   
//                o.timePairStack.add(tp);   
//            }   
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
                return o;   
    }   

	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
