/**
 * 
 */
package taichu.research.math.pi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * @author taichu
 *
 */
public class CaculatePi {
	
	//0.8是哈希结构的自动扩充因子，当达到80%装满后自动，一般也可不设定，让jvm自己处理
	private static Hashtable<String, String> algMap = new Hashtable<String, String>(50,(float)0.8);
	private static final int CACULATE_MAX_TIMES=2000000;
	
	//TODO:不确认对不对。
	//π=4/1-4/3+4/5-4/7+.   （CC： 通项为1/n^2）
	public static double algorithmGenPi1() {
		algMap.put("algorithmGenPi1","π=4/1-4/3+4/5-4/7+...");
		double pi = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			pi += Math.pow(-1, (i + 1)) * 4 / (2 * i - 1);
		}
		return pi;
	}
	
	//π²/6=1+1/2²+1/3²+1/4²+.   （CC： 通项为1/n^2）
	public static double algorithmGenPi2() {
		algMap.put("algorithmGenPi2","π²/6=1+1/2²+1/3²+1/4²+.");
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1/Math.pow(i, 2);
		}
		return Math.sqrt(tmp*6);
	}
	//π²/8=1+1/3²+1/5²+1/7²+.    （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi3() {
		algMap.put("algorithmGenPi3","π²/8=1+1/3²+1/5²+1/7²+.");
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1/Math.pow(2*i-1, 2);
		}
		return Math.sqrt(tmp*8);
	}
	
	//π²/24=1+1/2²+1/4²+1/6²+.    （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi4() {
		algMap.put("algorithmGenPi4","π²/24=1+1/2²+1/4²+1/6²+.");
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += 1/Math.pow(2*i, 2);
		}
		return Math.sqrt(tmp*24);
	}
	
	//π/4=1-1/3+1/5-1/7+.
	public static double algorithmGenPi5() {
		algMap.put("algorithmGenPi5","π/4=1-1/3+1/5-1/7+.");
		double tmp = 0.0d;
		for (int i = 1; i <= CACULATE_MAX_TIMES; i++) {
			tmp += Math.pow(-1, (i + 1)) * 1/(2*i-1);
		}
		return tmp*4;
	}
	
	
	/*
	 * java的反射用法：
	 */
	public static void RunAllAlgorithmGenPi(){
		//定义类类型
		Class<?> CaculatePi = null;
		try {
			//找到类
			CaculatePi = Class.forName("taichu.research.math.pi.CaculatePi");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Object obj = null;
		try {
			//申请类实例
			obj = CaculatePi.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//遍历类方法
		Method[] methods = CaculatePi.getDeclaredMethods();
		for(Method method: methods)
		{
			//找到符合要求的方法就执行；
			if (method.getName().startsWith("algorithmGenPi")) {
//			System.out.println("Got class Algorithm 'CaculatePi"+method.getName()+"', and call it!");
			try {
				double ret = (double)method.invoke(obj);
				System.out.println("PI=" + ret+",caculated by["+algMap.get(method.getName())+"]");
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
			}
		}
	}

	public static void main(String[] args) {
//		System.out.println("PI=" + String.valueOf(algorithmGenPi1()));
//		System.out.println("PI=" + String.valueOf(algorithmGenPi2()));
//		System.out.println("PI=" + String.valueOf(algorithmGenPi3()));
//		System.out.println("PI=" + String.valueOf(algorithmGenPi4()));
		
		RunAllAlgorithmGenPi();
	}
}
// 运行结果：PI=3.1415876535897618
/*
 * π/4=1-1/3+1/5-1/7+.   （CC：有正负交替项，暂无通项）
π²/6=1+1/2²+1/3²+1/4²+.   （CC： 通项为1/n^2）
π²/8=1+1/3²+1/5²+1/7²+.    （CC：通项为1/(2n-1)^2）
π²/12=1-1/2²+1/3²-1/4²+. （CC：有正负交替项，暂无通项）
π³/32=1-1/3³+1/5³-1/7³+.（CC：有正负交替项，暂无通项）
.
π/2=(2/1)(2/3)(4/3)(4/5).   （CC：通项为[(2n/2n-1)(2n/2n+1)] ）
.
4/π=1+1/(2+9/(2+25/(2+49/(2+.))))（CC：有递归/迭代项，暂无通项）

π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) - (4/(12*13*14) ...
（CC：有正负交替项，暂无通项）

*/
