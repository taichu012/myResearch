package taichu.research.ai.freelogic.entity.cell;



public class Cell implements Cloneable {
	
	//TODO 细胞老死
	private void Die(){
		
	}
	
	//TODO 新建细胞
	public Cell(){
	}
	
	//细胞自发的分裂
	private Cell DoDivision(Cell old){
		
		//TODO 暂时通过clone函数认为是细胞分裂
		//一般分为2种：（1）java语法上需要的clone；（2）逻辑业务上的“细胞分裂”，下面的实现需要修改！
		return (Cell) old.clone();
	}
	
	//下面实现需要根据Cell具体成员数据而修改！
	//TODO
	public Object clone() {   
		Cell o = null;   
        try {   
            o = (Cell) super.clone();   
            //将clone进行到底
            //根据需要将Cell内部的元素和数据都clone出来。
//            for(int i=0;i<timePairStack.size();i++){  
//            	TimePair tp=(TimePair) timePairStack.get(i).clone();//当然Class B也要实现相应clone方法   
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
