/**
 * 
 */
package taichu.research.tool;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * @author taichu
 *
 */
public class Test {

	private static Logger log = Logger.getLogger(Test.class);

	// 函数接口只能运行定义一个内部函数形态
	// 可以不加逐个annotation
	// @FunctionalInterface
	public interface Str2Str {
		String str2Str(String data);
	}

	// TODO：仿造对map中的多个算法进行多伦测试，来撰写一个检测MD5是否碰撞的测试函数

	// 对String2String接口的map-进行调用X次的测试；
	public static void RunXTimesByMap(ConcurrentHashMap<String, Str2Str> funcMap, String str, long runTimes) {
		Str2Str func = null;
		// 下面迭代是按照hash次序，并非是类似集合collection按照key来sort排序的。
		for (Entry<String, Str2Str> item : funcMap.entrySet()) {
			log.debug("func()=" + item.getKey());
			func = item.getValue();
			RunXTimes(func, str, runTimes);
		}
	}

	// 对String2String接口进行调用X次的测试；
	public static void RunXTimes(Str2Str itf, String str, long runTimes) {
		int i = 0;
		long intervalMs = 0l;
		long t0 = System.nanoTime();
		for (; i <= runTimes; i++) {
			itf.str2Str(str);
		}
		intervalMs = T.Time.getMsTnterval(t0, System.nanoTime());
		assert (intervalMs > 0);
		log.debug("Run func() " + runTimes + " times cost (" + intervalMs + ") ms，约"
				+ runTimes * 1000 / (double) intervalMs + " CAPS.");
	}


	public static void main(String[] args) {
		testGenMd5();
	}

	public static void testGenMd5() {

		// 将待测试的MD5生成算法通过lambda形式逐个添加到算法数组中
		ConcurrentHashMap<String, Str2Str> funcMap = new ConcurrentHashMap<String, Str2Str>();
		funcMap.put("T.Security.genMd5WithBytes32", (data) -> {
			return T.Security.genMd5WithBytes32(data);
		});
		funcMap.put("T.Security.genMd5WithBytes16", (data) -> {
			return T.Security.genMd5WithBytes16(data);
		});
		funcMap.put("T.Security.genMd5WithBytes8", (data) -> {
			return T.Security.genMd5WithBytes8(data);
		});
		funcMap.put("T.Security.genMd5WithBytes32_slow", (data) -> {
			return T.Security.genMd5WithBytes32_Slow(data);
		});

		String Byte100 = "0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ";

		// 对每个算法执行测试；
		// Str2Str x=(data) -> {return
		// T.Security.genMd5WithBytes32_Slow(data);};
		RunXTimes((data) -> {
			return T.Security.genMd5WithBytes32(data);
		}, Byte100, 1);
		RunXTimesByMap(funcMap, Byte100, 1);
		RunXTimesByMap(funcMap, Byte100, 1000000);
	}
	
	
	//有点像自己开发的JUNIT，只是尝试
	public static class Testor {

		public interface TestCase {
			void test();
		}

		// 运行X次test case；
		public static void RunTestCase(TestCase tc, long runTimes) {
			int i = 0;
			long intervalMs = 0l;
			long t0 = System.nanoTime();
			for (; i <= runTimes; i++) {
				tc.test();
			}
			intervalMs = T.Time.getMsTnterval(t0, System.nanoTime());
			assert (intervalMs > 0);
			log.debug("Test case() run " + runTimes + " times, cost (" + intervalMs + ") ms, about"
					+ runTimes * 1000 / (double) intervalMs + " CAPS.");
		}

		// 对String2String接口的map-进行调用X次的测试；
		public static void RunTestCases(ConcurrentHashMap<String, TestCase> testCaseMap, long runTimes) {
			TestCase tc = null;
			// 下面迭代是按照hash次序，并非是类似集合collection按照key来sort排序的。
			for (Entry<String, TestCase> item : testCaseMap.entrySet()) {
				log.debug("Test Case()=" + item.getKey());
				tc = item.getValue();
				RunTestCase(tc, runTimes);
			}
		}
	}

}
