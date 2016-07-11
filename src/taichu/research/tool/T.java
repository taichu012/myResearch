/**
 * 
 */
package taichu.research.tool;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecord;

/**
 * @author taichu
 *
 */
public class T {

	//NOTICE: define sub topic as "reflect" here as public variable
	//        then, define a inner class named "reflect" to hold function for sub topic
	//        why to do this? it's easy and obviously to use as T.getT().subtopic.functionX();
	public static Reflect reflect = null;
	public static Time time=null;


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
		
		public String OutputClassFieldsAsCsvLine(Class<?> clazz){
			Field[] fs = clazz.getDeclaredFields();  
			StringBuilder csvLine=new StringBuilder();
			for(int i = 0 ; i < fs.length; i++){  
				Field f = fs[i];  
//			    f.setAccessible(true); //设置些属性是可以访问的  
//			    Object val = f.get(bean);//得到此属性的值     
				csvLine.append(f.getName()+',');
			}
			return csvLine.toString();
		}
		
		
		public String OutputEntityFieldsAsCsvLine(Object obj){
			Field[] fs = obj.getClass().getDeclaredFields();  
			StringBuilder csvHeadLine = new StringBuilder();
			StringBuilder csvBodyLine = new StringBuilder();
			Object value=null;
			for(int i = 0 ; i < fs.length; i++){  
				Field f = fs[i];  
			    f.setAccessible(true); //设置些属性是可以访问的  
			    try {
					value = f.get(obj);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//得到此属性的值     
				csvHeadLine.append(f.getName()+',');
				if (value==null) value="<null>";
				csvBodyLine.append(value.toString()+',');
			}
			return csvHeadLine.toString()+"\r\n"+csvBodyLine.toString();
		}
		
		public Object InputCsvLine2Entity(String csvLine, Class<?> clazz){
			
			Object ret=null;
			if (csvLine==null||csvLine.length()==0) return ret;

			//split csvLine into values String[] in order left2right
			String[] values=csvLine.split(",");//CVS use ',' as delimiter. 
			
			//get fields from class object in order nature of Class source code.
			ret = getObject(clazz);
			//Below funtion NEED use Object NOT Class to call 
			Field[] fs = ret.getClass().getDeclaredFields(); 

			for(int i = 0 ; i < fs.length; i++){  
				Field f = fs[i];  
			    f.setAccessible(true); //设置些属性是可以访问的  
			    try {
					f.set(ret,values[i]);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//得到此属性的值     
			}
			return ret;
		}
		
		public static void main(String[] args){
			
			Reflect r=new Reflect();
			//test OutputBeanFieldsAsCsvLine
			System.out.println(r.OutputClassFieldsAsCsvLine(
					r.getClazz("taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecord")));
			System.out.println(r.OutputEntityFieldsAsCsvLine(new taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecordLineBasedString()));
			System.out.println(r.OutputEntityFieldsAsCsvLine(new taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecord()));
			String cvsLine="<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>";
			taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecord record =
					new taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity.VehiclePassingRecord();
			record=(VehiclePassingRecord) r.InputCsvLine2Entity(cvsLine,record.getClass());
			if (record!=null){
				System.out.println(r.OutputEntityFieldsAsCsvLine(record));
				
			}
		}
		
	}
	
	
	
	public void test() {
		//
	}

	public static class Time {

		public Time() {
		}

		public static Time getTime() {
			if (time == null) {
				time = new Time();
			}
			return time;
		}

		//test nanoTime and BigDecimal
		public static void main(String[] args) {
			long start = System.nanoTime();// 纳秒
			System.out.println(start);
			long end = System.nanoTime();// 纳秒
			System.out.println(end);
			BigDecimal diff = BigDecimal.valueOf(end - start, 10);// 秒级差值
			System.out.println(diff);
			BigDecimal result = diff.setScale(9, RoundingMode.HALF_UP);// 调节精度
			System.out.println(result);
			java.text.DecimalFormat fmt = new DecimalFormat("#.#########s");// 输出格式化
			System.out.println(fmt.format(result));
		}
	}

}
