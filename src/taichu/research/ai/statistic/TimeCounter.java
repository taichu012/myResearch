package taichu.research.ai.statistic;

import java.util.Set;
import java.util.Stack;

public class TimeCounter implements Cloneable  {
	
	//一个记录时间pair（开始，结束）的栈，自动增长，从不删除。
	
	 private Stack<TimePair> timePairStack = new Stack<TimePair>();  

	
	 public Stack<TimePair> getTimePairStack() {
		return timePairStack;
	}


	public boolean NeedEnd() {
		TimePair tp = timePairStack.peek();
		 if (tp!=null){
			 return (tp.getEndTime()==0L);
		 }else {
			 return false;
		 }
	}


	public int Size(){
		return timePairStack.size();
	}
	
	public void PushEndTime(long t){
		//将结束时间添加在缺少endtime的最后一个timepair上，其他情况都简单ignore不做任何处理
		if (this.Size()>0){
			TimePair tp=timePairStack.peek();
			if (tp.getEndTime()==0L){
				tp.setEndTime(t);
			}else{
				return;
			}
		}else {
			//空栈，有问题，不处理
			return;
		}
	}
	
	public void PushStartTime(long t){
		this.timePairStack.push(new TimePair(t,0L));	
	}
	
	public Object clone() {   
		TimeCounter o = null;   
        try {   
            o = (TimeCounter) super.clone();   
            o.timePairStack = new Stack<TimePair>();//将clone进行到底   
            for(int i=0;i<timePairStack.size();i++){  
            	TimePair tp=(TimePair) timePairStack.get(i).clone();//当然Class B也要实现相应clone方法   
                o.timePairStack.add(tp);   
            }   
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
                return o;   
    }   

	
}

// Stack&Vector描述： http://mis.hwai.edu.tw/~kevin/MISProject/JAVAProject/chapter12/c12-3.htm