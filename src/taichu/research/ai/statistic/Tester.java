package taichu.research.ai.statistic;

import java.io.PrintStream;
import java.util.Stack;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PrintStream po = System.out;

		long t1 = 0L;
		long t2 = 0L;
		long d = 0L;
		long SLEEP = 500L;
		int MAX = 5;
		String FLAG = "cctest1";

		try {
		for (int i = 0; i < MAX; i++) {
				TimeCounterMgr.StartTimeCounter(FLAG);
//			po.println("Time Begin = [" + t1 + "]");
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TimeCounterMgr.EndTimeCounter(FLAG);
//			po.println("Time End = [" + t2 + "]");
//			po.println("Time duration(s) = [" + (t2 - t1) + "]");
		}
		} catch (TimeCounterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		po.println("TODO请转换这些TESTCASE为JUNIT");
		po.println("#########TEST BEGIN################");
		
		po.println("TEST1#### 【正常测试】");
		Stack<TimePair> timeCounterStack = TimeCounterMgr.GetTimeCounterByClone(FLAG).getTimePairStack();
		
		int Count =timeCounterStack.size();
		for (int i=0;i<Count; i++){
			TimePair tp=(TimePair) timeCounterStack.pop();
			po.println((i+1)+"次, Time duration(s) = [" + (tp.getEndTime() - tp.getStartTime()) + "]");
		}
		
		po.println("TEST2#### 【Stack已经清空，应该报异常】");
		try{
			for (int i=0;i<Count; i++){
				TimePair tp=(TimePair) timeCounterStack.pop();
				po.println((i+1)+"次, Time duration(s) = [" + (tp.getEndTime() - tp.getStartTime()) + "]");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		po.println("TEST3#### 【尝试再次克隆TimeCounter不会修改原始数据，下面应该有输出】");
		Stack<TimePair> stack = TimeCounterMgr.GetTimeCounterByClone(FLAG).getTimePairStack();
		
		int Count3 =stack.size();
		for (int i=0;i<Count; i++){
			TimePair tp=(TimePair) stack.pop();
			po.println((i+1)+"次, Time duration(s) = [" + (tp.getEndTime() - tp.getStartTime()) + "]");
		}
		
		po.println("#########TEST END################");

	}
	
	private void test1(){
		PrintStream po = System.out;

		long t1 = 0L;
		long t2 = 0L;
		long SLEEP = 1000L;
		int MAX = 30;

		for (int i = 0; i < MAX; i++) {
			t1 = System.currentTimeMillis();
			po.println("Time Begin = [" + t1 + "]");
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t2 = System.currentTimeMillis();
			po.println("Time End = [" + t2 + "]");
			po.println("Time duration(s) = [" + (t2 - t1) + "]");
		}
	}

}
