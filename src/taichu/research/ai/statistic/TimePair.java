package taichu.research.ai.statistic;

import java.util.Stack;


public class TimePair implements Cloneable{
	 public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private long startTime=0L;
	 private long endTime=0L;
	 
	public TimePair(long startTime, long endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Object clone() {   
		TimePair o = null;   
        try {   
            o = (TimePair) super.clone();   
            o.startTime=this.startTime;
            o.endTime=this.endTime;
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
                return o;   
    }   
 }
