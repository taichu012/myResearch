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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.network.netty4.VehiclePassingRecordCollector.vpr.VehiclePassingRecordSenderHandler;

/**
 * @author taichu
 *
 */
public class T {
	
	private static Logger log = Logger.getLogger(T.class);

	//NOTICE: 
	//T是总的帮助类，内部子静态类是分门别类的方便使用的，比如反射Reflect类，File，OSSys，Time等；
	//需要子类细分的请建立新的静态类（public static class）
	

	/**
	 * 
	 */
//	public T() {}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO：maybe need more test case in future
	}

	public static String GetPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		// System.out.println(name);
		// get pid from pid@domain
		return name.split("@")[0];
	}

	public static String GetPIDWithDomain() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		// System.out.println(name);
		// get pid@DOMAIN
		return name;
	}

	@SuppressWarnings("static-access")
	public static String GetThreadName(Thread t) {
		return t.currentThread().getName();
	}

	//long currentTime = System.currentTimeMillis();

	public static String getDateTimeNow() {
		return getDateTimeNow(System.currentTimeMillis());
	}
	
	public static String getDateTimeNow(long ms) {

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

		public static Class<?> getClazz(String className) {
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

		public static Object getObject(Class<?> clazz) {
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

		public static Method getMethod(Class<?> clazz, String methodName) {
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

		public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
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

		public static Object InvokeMethod(Object obj, Method m) {
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

		public static Object InvokeMethod(Object obj, Method m, Object[] args) {
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
		
		public static String genOneCsvLineFromClassFields(Class<?> clazz){
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
		
		
		public static String genTwoCsvLineFromBeanAttributesAndValues(Object obj){
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
		
		public static Object InputCsvLine2Entity(String csvLine, Class<?> clazz){
			
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
			
			//test OutputBeanFieldsAsCsvLine
			System.out.println(T.Reflect.genOneCsvLineFromClassFields(
					T.Reflect.getClazz("taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord")));
			System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(new taichu.research.network.netty4.VehiclePassingRecordCollector.notuse.VehiclePassingRecordBasedOnSmp()));
			System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord()));
			String cvsLine="<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>";
			taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord record =
					new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord();
			record=(VehiclePassingRecord) T.Reflect.InputCsvLine2Entity(cvsLine,record.getClass());
			if (record!=null){
				System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(record));
				
			}
		}
		
	}

	public static class Time {

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
		
		public static ConcurrentHashMap<String, String> getLinesWithMD5KeyFromFile(String filename){
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
		
		public static String genMD5(String data) { 
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

	public static class OSSys {


		public static void printOsSysProperties() {
			//具体属性含义见：http://marsvaadin.iteye.com/blog/1671046
			System.out.println("java_vendor:" + System.getProperty("java.vendor"));
			System.out.println("java_vendor_url:" + System.getProperty("java.vendor.url"));
			System.out.println("java_home:" + System.getProperty("java.home"));
			System.out.println("java_class_version:" + System.getProperty("java.class.version"));
			System.out.println("java_class_path:" + System.getProperty("java.class.path"));
			System.out.println("os_name:" + System.getProperty("os.name"));
			System.out.println("os_arch:" + System.getProperty("os.arch"));
			System.out.println("os_version:" + System.getProperty("os.version"));
			System.out.println("user_name:" + System.getProperty("user.name"));
			System.out.println("user_home:" + System.getProperty("user.home"));
			System.out.println("user_dir:" + System.getProperty("user.dir"));
			System.out.println("java_vm_specification_version:" + System.getProperty("java.vm.specification.version"));
			System.out.println("java_vm_specification_vendor:" + System.getProperty("java.vm.specification.vendor"));
			System.out.println("java_vm_specification_name:" + System.getProperty("java.vm.specification.name"));
			System.out.println("java_vm_version:" + System.getProperty("java.vm.version"));
			System.out.println("java_vm_vendor:" + System.getProperty("java.vm.vendor"));
			System.out.println("java_vm_name:" + System.getProperty("java.vm.name"));
			System.out.println("java_ext_dirs:" + System.getProperty("java.ext.dirs"));
			System.out.println("file_separator:" + System.getProperty("file.separator"));
			System.out.println("path_separator:" + System.getProperty("path.separator"));
			System.out.println("line_separator:" + System.getProperty("line.separator"));
		}
		
		/**
		 * 
		 * @param cls - 程序所在的任一功能类（不能是java/javax/或其他类库的类）
		 * @return 默认返回null
		 */
		public static String getAppPath(Class<?> cls) {
			if (cls == null){
				log.error("Wrong class name!");
				return null;
			}
			java.net.URL url = cls.getProtectionDomain().getCodeSource().getLocation();
			String filePath = null;
			try {
				filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Parse class path error!");
			}
			if (filePath.endsWith(".jar"))
				filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
			java.io.File file = new java.io.File(filePath);
			filePath = file.getAbsolutePath();
			return filePath;
		}

		/**
		 * 
		 * @param cls - 程序所在的任一功能类（不能是java/javax/或其他类库的类）
		 * @return 默认返回null
		 */
		public static String getRealPath(Class<?> cls) {
			if (cls == null){
				log.error("Wrong class name!");
				return null;
			}
			String realPath = cls.getClassLoader().getResource("").getFile();
			java.io.File file = new java.io.File(realPath);
			realPath = file.getAbsolutePath();
			try {
				realPath = java.net.URLDecoder.decode(realPath, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return realPath;
		}


		public static void main(String[] args) {
			System.out.println(getRealPath(T.class));
			System.out.println(getAppPath(T.class));
			printOsSysProperties();
			
		}
	}

}
