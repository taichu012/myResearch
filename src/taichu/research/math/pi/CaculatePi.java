/**
 * 
 */
package taichu.research.math.pi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author taichu
 *
 */
public class CaculatePi {

	public static void main(String[] args) {
		System.out.println("PI=3.14159265358979323846[真实]");
		RunAllAlgorithmGenPi();
	}

	// 0.8是哈希结构的自动扩充因子，当达到80%装满后自动，一般也可不设定，让jvm自己处理
	private static Hashtable<String, String> algMap = new Hashtable<String, String>(50, (float) 0.8);
	private static final int CACULATE_MAX_TIMES = 10000000;

	public CaculatePi() {
		//hashtable按照一定算法來放入key value对，所以枚举返回的时候也是这个次序，而不是添加的次序。
		algMap.put("algorithmGenPi1", "π=4/1-4/3+4/5-4/7+...");
		algMap.put("algorithmGenPi5", "π/4=1-1/3+1/5-1/7+.");
		algMap.put("algorithmGenPi4", "π²/24=1+1/2²+1/4²+1/6²+.");
		algMap.put("algorithmGenPi3", "π²/8=1+1/3²+1/5²+1/7²+.");
		algMap.put("algorithmGenPi2", "π²/6=1+1/2²+1/3²+1/4²+.");
		algMap.put("algorithmGenPi6", "π²/12=1-1/2²+1/3²-1/4²+.");
		algMap.put("algorithmGenPi7", "π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) - (4/(12*13*14) ...");
		algMap.put("algorithmGenPi8", "π/2=(2/1)(2/3)(4/3)(4/5).");
		algMap.put("algorithmGenPi9", "π³/32=1-1/3³+1/5³-1/7³+.");

	}
	
//TODO:待确认是否公式正确？
//  π³/32=1-1/3³+1/5³-1/7³+.（CC：有正负交替项，暂无通项） .
	public static double algorithmGenPi9() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += Math.pow(-1,i+1)/Math.pow((2*i-1),3);
		}
		return Math.pow(tmp*32,1/3);
	}
	
	//TODO:待确认是否公式正确？
//  π/2=(2/1)(2/3)(4/3)(4/5). （CC：通项为[(2n/2n-1)(2n/2n+1)] ） .
	public static double algorithmGenPi8() {
		double pi = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			pi *= ((2*i)/(2*i-1))*((2*i)/(2*i+1));
		}
		return pi*2;
	}
	
	//TODO:待确认是否公式正确？
//  π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) - (4/(12*13*14) ... （CC：有正负交替项，暂无通项）
	public static double algorithmGenPi7() {
		double pi = 3.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			pi += Math.pow(-1, i+1)*4/((2*i)*(2*i+1)*(2*i+2));
		}
		return pi;
	}
	
	
	
	//π²/12=1-1/2²+1/3²-1/4²+.
	public static double algorithmGenPi6() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += Math.pow(-1, (i + 1)) * 1 / Math.pow(i,2);
		}
		return Math.sqrt(tmp * 12);
	}

	// π=4/1-4/3+4/5-4/7+. （CC： 通项为1/n^2）
	public static double algorithmGenPi1() {
		double pi = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			pi += Math.pow(-1, (i + 1)) * 4 / (2 * i - 1);
		}
		return pi;
	}

	// π²/6=1+1/2²+1/3²+1/4²+. （CC： 通项为1/n^2）
	public static double algorithmGenPi2() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1 / Math.pow(i, 2);
		}
		return Math.sqrt(tmp * 6);
	}

	// π²/8=1+1/3²+1/5²+1/7²+. （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi3() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1 / Math.pow(2 * i - 1, 2);
		}
		return Math.sqrt(tmp * 8);
	}

	// π²/24=1+1/2²+1/4²+1/6²+. （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi4() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1 / Math.pow(2 * i, 2);
		}
		return Math.sqrt(tmp * 24);
	}

	// π/4=1-1/3+1/5-1/7+.
	public static double algorithmGenPi5() {
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += Math.pow(-1, (i + 1)) * 1 / (2 * i - 1);
		}
		return tmp * 4;
	}

	/*
	 * java的反射用法：
	 */
	public static void RunAllAlgorithmGenPi() {
		// 定义类类型
		Class<?> clazz = null;
		try {
			// 找到类
			clazz = Class.forName("taichu.research.math.pi.CaculatePi");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Object obj = null;
		try {
			// 申请类实例
			obj = clazz.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Set<String> keys = algMap.keySet();
		for (String key : keys) {
			Method method = null;
			try {
				method = clazz.getDeclaredMethod(key);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double ret = 0;
			try {
				ret = (double) method.invoke(obj);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("PI=" + ret + ", alg=[" + algMap.get(key) + "], 函数[" + key + "]");
		}
	}

}


// 运行结果：PI=3.1415876535897618

//  π/4=1-1/3+1/5-1/7+. （CC：有正负交替项，暂无通项） 
//  π²/6=1+1/2²+1/3²+1/4²+. （CC： 通项为1/n^2）
//  π²/8=1+1/3²+1/5²+1/7²+. （CC：通项为1/(2n-1)^2）
//  π²/24=1+1/2²+1/4²+1/6²+. (CC:发明）
//  π²/12=1-1/2²+1/3²-1/4²+.  （CC：有正负交替项，暂无通项） 
//  π³/32=1-1/3³+1/5³-1/7³+.（CC：有正负交替项，暂无通项） .
//  π/2=(2/1)(2/3)(4/3)(4/5). （CC：通项为[(2n/2n-1)(2n/2n+1)] ） .
//  π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) - (4/(12*13*14) ... （CC：有正负交替项，暂无通项）

// 4/π=1+1/(2+9/(2+25/(2+49/(2+.))))（CC：有递归/迭代项，暂无通项）
 
