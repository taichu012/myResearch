/**
 * 
 */
package taichu.research.tool;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author taichu
 *
 */
public class T {

	//NOTICE: define sub topic as "reflect" here as public variable
	//        then, define a inner class named "reflect" to hold function for sub topic
	//        why to do this? it's easy and obviously to use as T.getT().subtopic.functionX();
	public static Reflect reflect = null;


	/**
	 * 
	 */
	public T() {
		Reflect.getReflect();
	}
	private static T instance = null;

	public static T getT() {
		if (instance == null) {
			instance = new T();
		}
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO：maybe need more test case in future
	}

	public String GetPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		// System.out.println(name);
		// get pid from pid@domain
		return name.split("@")[0];
	}

	public String GetPIDWithDomain() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		// System.out.println(name);
		// get pid@DOMAIN
		return name;
	}

	@SuppressWarnings("static-access")
	public String GetThreadName(Thread t) {
		return t.currentThread().getName();
	}

	long currentTime = System.currentTimeMillis();

	public String getDateTimeFromCurrentTimeMillis() {

		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
		// Date date = new Date(System.currentTimeMillis());
		// return (formatter.format(date));
		return getDateTimeFromCurrentTimeMillis(System.currentTimeMillis());
	}

	public String getDateTimeFromCurrentTimeMillis(long ms) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
		Date date = new Date(ms);
		return (formatter.format(date));
	}

	// byte 数组与 long 的相互转换
	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}


	
	public static class Reflect {
		
		public Reflect(){}
		public static Reflect getReflect() {
			if (reflect == null) {
				reflect = new Reflect();
			}
			return reflect;
		}
		
		/*
		 * java反射相关工具类
		 */

		public Class<?> getClazz(String className) {
			// 定义类类型
			Class<?> clazz = null;
			try {
				// 找到类
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			}
			return clazz;
		}

		public Object getObject(Class<?> clazz) {
			Object obj = null;
			try {
				// 申请类实例
				obj = clazz.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			return obj;
		}

		public Method getMethod(Class<?> clazz, String methodName) {
			Method m = null;
			try {
				m = clazz.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return m;
		}

		public Method getMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
			Method m = null;
			try {
				m = clazz.getDeclaredMethod(methodName, argTypes);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return m;
		}

		public Object InvokeMethod(Object obj, Method m) {
			Object ret = null;
			if ((m != null) && (obj != null)) {
				try {
					ret = (double) m.invoke(obj);
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
			return ret;

		}

		public Object InvokeMethod(Object obj, Method m, Object[] args) {
			Object ret = null;
			if ((m != null) && (obj != null)) {
				try {
					ret = (double) m.invoke(obj, args);
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
			return ret;
		}
	}
	
	public void test() {
		//
	}

}
