/**
 * 
 */
package taichu.research.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;

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
	public static File file=null;


	/**
	 * 
	 */
	public T() {
		Reflect.getReflect();
		File.getFile();
		Time.getTime();
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
					r.getClazz("taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord")));
			System.out.println(r.OutputEntityFieldsAsCsvLine(new taichu.research.network.netty4.VehiclePassingRecordCollector.protocal.VehiclePassingRecordBasedOnSmp()));
			System.out.println(r.OutputEntityFieldsAsCsvLine(new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord()));
			String cvsLine="<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>";
			taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord record =
					new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord();
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
	
	public static class File {
		
		public File(){}
		public static File getFile() {
			if (file == null) {
				file = new File();
			}
			return file;
		}
		
		public ConcurrentHashMap<String, String> getLinesFromFile(String filename){
			ConcurrentHashMap<String, String> linesMap = new ConcurrentHashMap<String, String>(); 
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(filename));
				String line=null;
				while ((line = reader.readLine()) != null) {
					String md5 = genMD5(line);
					linesMap.put(md5, line);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Doesn't find Csv file(" + filename + ")!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Read Csv file(" + filename + ") error!");
			} finally {
				reader = null;
			}
			return linesMap;
		}
		
		public String genMD5(String data) { 
	        MessageDigest md = null;
	        StringBuffer buf = new StringBuffer(); 
			try {
				md = MessageDigest.getInstance("MD5");
		        md.update(data.getBytes()); 
		        byte[] bits = md.digest(); 
		        for(int i=0;i<bits.length;i++){ 
		            int a = bits[i]; 
		            if(a<0) a+=256; 
		            if(a<16) buf.append("0"); 
		            buf.append(Integer.toHexString(a)); 
		        }
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} 
	        return buf.toString(); 
	    } 


		
		/*
		 * cvs相关工具类
		 */
		//test nanoTime and BigDecimal
		public static void main(String[] args) {
		}
	}
	

}
