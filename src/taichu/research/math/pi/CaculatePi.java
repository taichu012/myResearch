/**
 * 
 */
package taichu.research.math.pi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public class CaculatePi {

	public static void main(String[] args) {
		System.out.println("PI=3.14159265358979323846[真实]");
		initCaculatePi();
//		 testAllAlg();
		 testOneAlg("algorithmGenPi9");

	}

	public static void testOneAlg(String key) {
		String value = algMap.get(key);
		int max = 500000;
		int max_real =5000000;
		int delta = 500000;
		Class<?> clazz = T.getT().reflect.getClazz("taichu.research.math.pi.CaculatePi");
		Object obj=T.getT().reflect.getObject(clazz);
		Method m = T.getT().reflect.getMethod(clazz,key,new Class<?>[]{Integer.class});
		if ((m!=null)&&(obj!=null))
		for (int j = 1; j <= max_real; j = j + delta) {
			double piTemp = (double) T.getT().reflect.InvokeMethod(obj,m,new Object[]{j});
			System.out.println("PI=" + piTemp 
						+ ", alg=[" + key + "], 迭代计算次数[" + (j-1) + "], 和真实PI相差["
						+(PI_PUBLIC-piTemp)/PI_PUBLIC*100+"%, "+(PI_PUBLIC-piTemp));
		}
	}

	// 0.8是哈希结构的自动扩充因子，当达到80%装满后自动，一般也可不设定，让jvm自己处理
	private static HashMap<String, String> algMap = new HashMap<String, String>(50, (float) 0.8);
	private static List<Map.Entry<String, String>> algMapList = null;

	private static int CACULATE_MAX_TIMES = 50000;
	
	private static final double PI_PUBLIC=3.14159265358979323846d;

	public static void initCaculatePi() {
		// hashmap按照一定算法來放入key value对，所以枚举返回的时候也是这个次序，而不是添加的次序。
		algMap.put("algorithmGenPi1", "π=4/1-4/3+4/5-4/7+...");
		algMap.put("algorithmGenPi5", "π/4=1-1/3+1/5-1/7+.");
		algMap.put("algorithmGenPi4", "π²/24=1+1/2²+1/4²+1/6²+.");
		algMap.put("algorithmGenPi3", "π²/8=1+1/3²+1/5²+1/7²+.");
		algMap.put("algorithmGenPi2", "π²/6=1+1/2²+1/3²+1/4²+.");
		algMap.put("algorithmGenPi6", "π²/12=1-1/2²+1/3²-1/4²+.");
		algMap.put("algorithmGenPi7",
				"π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10)...");
		algMap.put("algorithmGenPi8", "π/2=(2/1)(2/3)(4/3)(4/5).");
		algMap.put("algorithmGenPi9", "π³/32=1-1/3³+1/5³-1/7³+.");

		algMapList = new ArrayList<Map.Entry<String, String>>(algMap.entrySet());

		// 排序
		Collections.sort(algMapList, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				// 根据value值排序
				// return (o2.getValue() - o1.getValue());
				// 根据key值排序
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

	}

	// TODO:待确认是否公式正确？好像公式错误，明显等式右面收敛于1；
	// π³/32=1-1/3³+1/5³-1/7³+.（CC：有正负交替项，暂无通项） .
	public static double algorithmGenPi9(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += Math.pow(-1, i + 1) / Math.pow((2 * i - 1), 3);
		}
		return Math.pow(tmp * 32, 1/3);
	}

	// NOTICE：公式在一定范围内逼近PI，超过则严重发散！这是不是说它在逼近PI的时候就更快？更陡？TODO：待研究；
	//一般的，在5000到50000万次迭代的时候效果比较明显。
	// π/2=1*(2/1)(2/3)(4/3)(4/5). （CC：通项为[(2n/2n-1)(2n/2n+1)，通项公式是两项的，可能不对！] ） .
	// 如下实现是按照单项的方法计算
	public static double algorithmGenPi8(Integer iterTimes) {
		double tmp = 1.0d;
		double sum = 1.0d;
		for (int i = 2; i <= iterTimes; i=i+2) {
			tmp = (double)(i*i)/((i-1)*(i+1));
			sum*=tmp;
		}
		return sum * 2;
	}

	// 特别注意：此算法进过测试，越来越发散！！！
	// π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) -
	// (4/(12*13*14) ... （CC：有正负交替项，暂无通项）
	public static double algorithmGenPi7(Integer iterTimes) {
		double pi = 3.0d;
		for (int i = 1; i <= iterTimes; i++) {
			pi += Math.pow(-1, i + 1) * 4 / ((2 * i) * (2 * i + 1) * (2 * i + 2));
		}
		return pi;
	}

	// π²/12=1-1/2²+1/3²-1/4²+.
	public static double algorithmGenPi6(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += Math.pow(-1, (i + 1)) * 1 / Math.pow(i, 2);
		}
		return Math.sqrt(tmp * 12);
	}

	// π=4/1-4/3+4/5-4/7+. （CC： 通项为1/n^2）
	public static double algorithmGenPi1(Integer iterTimes) {
		double pi = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			pi += Math.pow(-1, (i + 1)) * 4 / (2 * i - 1);
		}
		return pi;
	}

	// π²/6=1+1/2²+1/3²+1/4²+. （CC： 通项为1/n^2）
	public static double algorithmGenPi2(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += 1 / Math.pow(i, 2);
		}
		return Math.sqrt(tmp * 6);
	}

	// π²/8=1+1/3²+1/5²+1/7²+. （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi3(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += 1 / Math.pow(2 * i - 1, 2);
		}
		return Math.sqrt(tmp * 8);
	}

	// π²/24=1+1/2²+1/4²+1/6²+. （CC：通项为1/(2n-1)^2）
	public static double algorithmGenPi4(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += 1 / Math.pow(2 * i, 2);
		}
		return Math.sqrt(tmp * 24);
	}

	// π/4=1-1/3+1/5-1/7+.
	public static double algorithmGenPi5(Integer iterTimes) {
		double tmp = 0.0d;
		for (int i = 1; i <= iterTimes; i++) {
			tmp += Math.pow(-1, (i + 1)) * 1 / (2 * i - 1);
		}
		return tmp * 4;
	}

	/*
	 * java的反射用法：
	 */
	public static void testAllAlg() {
		// 定义类类型
		Class<?> clazz = null;
		String className = "taichu.research.math.pi.CaculatePi";
		clazz = T.getT().reflect.getClazz(className);
		if (clazz == null) {
			System.out.println("Got class[" + className + "] failed! programe exit!");
			return;
		}

		Object obj = null;
		obj = T.getT().reflect.getObject(clazz);
		if (obj == null) {
			System.out.println("Got Object of [" + className + "] failed! programe exit!");
			return;
		}

		String key = null;
		String value = null;
		for (Map.Entry<String, String> map : algMapList) {
			// System.out.println(map.getKey() + ":" + map.getValue());
			key = map.getKey();
			value = map.getValue();

			Method method = null;
			method = T.getT().reflect.getMethod(clazz, key, new Class<?>[]{Integer.class});
			if (clazz == null) {
				System.out.println("Got method[" + key + "] failed! programe exit!");
				return;
			}
			double ret = 0;
//			ret = (double) T.getT().reflect.InvokeMethod(obj, method);
			//TODO 增加输入值CACULATE_MAX_TIMES
			Object[] args=new Object[]{(Object)CACULATE_MAX_TIMES};
			ret = (double) T.getT().reflect.InvokeMethod(obj, method,args);
			System.out.println("PI=" + ret + ", alg=[" + value + "], 函数[" + key + "]");
		}
	}

}

// 标准数学PI近似值：3.14159265358979323846

// π/4=1-1/3+1/5-1/7+. （CC：有正负交替项，暂无通项）
// π²/6=1+1/2²+1/3²+1/4²+. （CC： 通项为1/n^2）
// π²/8=1+1/3²+1/5²+1/7²+. （CC：通项为1/(2n-1)^2）
// π²/24=1+1/2²+1/4²+1/6²+. (CC:自己发明）
// π²/12=1-1/2²+1/3²-1/4²+. （CC：有正负交替项，暂无通项）
// π³/32=1-1/3³+1/5³-1/7³+.（CC：有正负交替项，暂无通项） .
// π/2=(2/1)(2/3)(4/3)(4/5). （CC：通项为[(2n/2n-1)(2n/2n+1)] ） .
// π = 3 + 4/(2*3*4) - 4/(4*5*6) + 4/(6*7*8) - 4/(8*9*10) + 4/(10*11*12) -
// (4/(12*13*14) ... （CC：有正负交替项，暂无通项）

// 4/π=1+1/(2+9/(2+25/(2+49/(2+.))))（CC：有递归/迭代项，暂无通项），程序实现容易死机，需要专门编制的算法。
