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

/**
 * @author taichu
 *
 */
public class T {

	private static Logger log = Logger.getLogger(T.class);

	// NOTICE:
	// T是总的帮助类，内部子静态类是分门别类的方便使用的，比如反射Reflect类，File，OS，Time等；
	// 需要子类细分的请建立新的静态类（public static class）

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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

		public static String genOneCsvLineFromClassFields(Class<?> clazz) {
			Field[] fs = clazz.getDeclaredFields();
			StringBuilder csvLine = new StringBuilder();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				// f.setAccessible(true); //设置些属性是可以访问的
				// Object val = f.get(bean);//得到此属性的值
				csvLine.append(f.getName() + ',');
			}
			return csvLine.toString();
		}

		public static String genTwoCsvLineFromBeanAttributesAndValues(Object obj) {
			Field[] fs = obj.getClass().getDeclaredFields();
			StringBuilder csvHeadLine = new StringBuilder();
			StringBuilder csvBodyLine = new StringBuilder();
			Object value = null;
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				f.setAccessible(true); // 设置些属性是可以访问的
				try {
					value = f.get(obj);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 得到此属性的值
				csvHeadLine.append(f.getName() + ',');
				if (value == null)
					value = "<null>";
				csvBodyLine.append(value.toString() + ',');
			}
			return csvHeadLine.toString() + "\r\n" + csvBodyLine.toString();
		}

		public static Object InputCsvLine2Entity(String csvLine, Class<?> clazz) {

			Object ret = null;
			if (csvLine == null || csvLine.length() == 0)
				return ret;

			// split csvLine into values String[] in order left2right
			String[] values = csvLine.split(",");// CVS use ',' as delimiter.

			// get fields from class object in order nature of Class source
			// code.
			ret = getObject(clazz);
			// Below funtion NEED use Object NOT Class to call
			Field[] fs = ret.getClass().getDeclaredFields();

			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				f.setAccessible(true); // 设置些属性是可以访问的
				try {
					f.set(ret, values[i]);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 得到此属性的值
			}
			return ret;
		}

		public static void main(String[] args) {

			// test OutputBeanFieldsAsCsvLine
			System.out.println(T.Reflect.genOneCsvLineFromClassFields(T.Reflect.getClazz(
					"taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord")));
			System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(
					new taichu.research.network.netty4.VehiclePassingRecordCollector.notuse.VehiclePassingRecordBasedOnSmp()));
			System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(
					new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord()));
			String cvsLine = "<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>,<null>";
			taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord record = new taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord();
			record = (VehiclePassingRecord) T.Reflect.InputCsvLine2Entity(cvsLine, record.getClass());
			if (record != null) {
				System.out.println(T.Reflect.genTwoCsvLineFromBeanAttributesAndValues(record));

			}
		}

	}

	public static class Time {

		// test nanoTime and BigDecimal
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
		
		/**
		 * 
		 * @param beginNs - 开始nanotime
		 * @param endNs - 结束nanotime
		 * @return 时间间隔毫秒
		 */
		public static long getMsTnterval(long beginNs,long endNs){
			return (endNs-beginNs)/1000/1000;
		}

		// long currentTime = System.currentTimeMillis();
		
		public static String getDateTimeNow() {
			return getDateTimeNow(System.currentTimeMillis());
		}

		public static String getDateTimeNow(long ms) {
		
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
			Date date = new Date(ms);
			return (formatter.format(date));
		}
	}

	public static class File {

		public static ConcurrentHashMap<String, String> getLinesWithMD5KeyFromFile(String filename) {
			ConcurrentHashMap<String, String> linesMap = new ConcurrentHashMap<String, String>();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(filename));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String md5 = Security.genMd5WithBytes32(line);
					linesMap.put(md5, line);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Doesn't find Csv file(" + filename + ")!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Read Csv file(" + filename + ") error!");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Gen MD5 error!");
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
				for (int i = 0; i < bits.length; i++) {
					int a = bits[i];
					if (a < 0)
						a += 256;
					if (a < 16)
						buf.append("0");
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
		// test nanoTime and BigDecimal
		public static void main(String[] args) {
		}
	}

	public static class OS {

		public static void printOsProperties() {
			// 具体属性含义见：http://marsvaadin.iteye.com/blog/1671046
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
		 * @param cls
		 *            - 程序所在的任一功能类（不能是java/javax/或其他类库的类）
		 * @return 默认返回null
		 */
		public static String getAppPath(Class<?> cls) {
			if (cls == null) {
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
		 * @param cls
		 *            - 程序所在的任一功能类（不能是java/javax/或其他类库的类）
		 * @return 默认返回null
		 */
		public static String getRealPath(Class<?> cls) {
			if (cls == null) {
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
			printOsProperties();

		}
	}

	public static class Security {

		public static String genMd5WithBytes32_Slow(String data) {
			MessageDigest md = null;
			StringBuffer buf = new StringBuffer();
			try {
				md = MessageDigest.getInstance("MD5");
				md.update(data.getBytes());
				byte[] bits = md.digest();
				for (int i = 0; i < bits.length; i++) {
					int a = bits[i];
					if (a < 0)
						a += 256;
					if (a < 16)
						buf.append("0");
					buf.append(Integer.toHexString(a));
				}
			} catch (NoSuchAlgorithmException e) {
				log.error("NoSuchAlgorithmException！无此算法.");
				e.printStackTrace();
				return null;
			}
			return buf.toString();
		}

		private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };

		private static String toHexString(byte[] b) {
			StringBuilder sb = new StringBuilder(b.length * 2);
			for (int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			return sb.toString();
		}

		/*
		 * 超过10万
		 */
		public static String genMd5WithBytes32(String data) {
			MessageDigest digest=null;
			try {
				digest = java.security.MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				log.error("NoSuchAlgorithmException！无此算法.");
				e.printStackTrace();
			}
			digest.update(data.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		}

		public static String genMd5WithBytes16(String data) {
			return genMd5WithBytes32(data).substring(8, 24);
		}

		public static String genMd5WithBytes8(String data) {
			return genMd5WithBytes32(data).substring(12, 20);
		}

		/*
		 * cvs相关工具类
		 */
		// test nanoTime and BigDecimal
		public static void main(String[] args) {
			//测试2个MD5算法的生成速度；
			String Byte100 ="0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ";
			long t0=System.nanoTime();
			int i=0;
			long maxTry=1000000l;
			long delta =0l;
			
			for (;i<=maxTry;i++){
				Security.genMd5WithBytes32_Slow(Byte100);
			}
			delta=(System.nanoTime()-t0);
			assert(delta>0);
			log.debug("genMD5() gen MD5("+Security.genMd5WithBytes32_Slow(Byte100)+" "+i+" times cost ("
					+ delta+") ns，约"+maxTry*1000*1000*1000/delta+"CAPS.");

			try {
				i=0;
				t0=System.nanoTime();
				for (;i<=maxTry;i++){
					Security.genMd5WithBytes32(Byte100);
				}
				assert(delta>0);
				delta=(System.nanoTime()-t0);
				log.debug("Bytes32() gen MD5("+Security.genMd5WithBytes32(Byte100)+" "+i+" times cost ("
						+ (System.nanoTime()-t0)+") ns，约"+maxTry*1000*1000*1000/delta+"CAPS.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				//断言两个方法相同；
				//在eclipse的run方法中“VM”的arguments参数增加“-ea”，来开启assert报错（如果false）
				assert(Security.genMd5WithBytes32_Slow(Byte100).equals(Security.genMd5WithBytes32(Byte100)));
				//下面断言必定是错误的，如果开启“-ea”的VM参数，则会报错；
				assert(!Security.genMd5WithBytes32(Byte100).equals("MUST BE FAILED！"));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//测试8位MD5生成算法（生成32位然后截取8位）的碰撞情况；
			//计划算法；那一段类似协议中消息的字符串数据作为data的基础，不断替换其中的某些部分，
			//替换的部分可以随机，但应保证最终的data永远不重复，这样测试MD5生成了32字节截取其中8字节的碰撞概率
			//期望是0！ TODO
		}

	}

	public static class Crc {

		/**
		 * CRC8 algorithm  encode: utf-8 
		 */
		public static class CRC8_Algm1 {
			static byte[] crc8_tab = { (byte) 0, (byte) 94, (byte) 188, (byte) 226, (byte) 97, (byte) 63, (byte) 221,
					(byte) 131, (byte) 194, (byte) 156, (byte) 126, (byte) 32, (byte) 163, (byte) 253, (byte) 31,
					(byte) 65, (byte) 157, (byte) 195, (byte) 33, (byte) 127, (byte) 252, (byte) 162, (byte) 64,
					(byte) 30, (byte) 95, (byte) 1, (byte) 227, (byte) 189, (byte) 62, (byte) 96, (byte) 130,
					(byte) 220, (byte) 35, (byte) 125, (byte) 159, (byte) 193, (byte) 66, (byte) 28, (byte) 254,
					(byte) 160, (byte) 225, (byte) 191, (byte) 93, (byte) 3, (byte) 128, (byte) 222, (byte) 60,
					(byte) 98, (byte) 190, (byte) 224, (byte) 2, (byte) 92, (byte) 223, (byte) 129, (byte) 99,
					(byte) 61, (byte) 124, (byte) 34, (byte) 192, (byte) 158, (byte) 29, (byte) 67, (byte) 161,
					(byte) 255, (byte) 70, (byte) 24, (byte) 250, (byte) 164, (byte) 39, (byte) 121, (byte) 155,
					(byte) 197, (byte) 132, (byte) 218, (byte) 56, (byte) 102, (byte) 229, (byte) 187, (byte) 89,
					(byte) 7, (byte) 219, (byte) 133, (byte) 103, (byte) 57, (byte) 186, (byte) 228, (byte) 6,
					(byte) 88, (byte) 25, (byte) 71, (byte) 165, (byte) 251, (byte) 120, (byte) 38, (byte) 196,
					(byte) 154, (byte) 101, (byte) 59, (byte) 217, (byte) 135, (byte) 4, (byte) 90, (byte) 184,
					(byte) 230, (byte) 167, (byte) 249, (byte) 27, (byte) 69, (byte) 198, (byte) 152, (byte) 122,
					(byte) 36, (byte) 248, (byte) 166, (byte) 68, (byte) 26, (byte) 153, (byte) 199, (byte) 37,
					(byte) 123, (byte) 58, (byte) 100, (byte) 134, (byte) 216, (byte) 91, (byte) 5, (byte) 231,
					(byte) 185, (byte) 140, (byte) 210, (byte) 48, (byte) 110, (byte) 237, (byte) 179, (byte) 81,
					(byte) 15, (byte) 78, (byte) 16, (byte) 242, (byte) 172, (byte) 47, (byte) 113, (byte) 147,
					(byte) 205, (byte) 17, (byte) 79, (byte) 173, (byte) 243, (byte) 112, (byte) 46, (byte) 204,
					(byte) 146, (byte) 211, (byte) 141, (byte) 111, (byte) 49, (byte) 178, (byte) 236, (byte) 14,
					(byte) 80, (byte) 175, (byte) 241, (byte) 19, (byte) 77, (byte) 206, (byte) 144, (byte) 114,
					(byte) 44, (byte) 109, (byte) 51, (byte) 209, (byte) 143, (byte) 12, (byte) 82, (byte) 176,
					(byte) 238, (byte) 50, (byte) 108, (byte) 142, (byte) 208, (byte) 83, (byte) 13, (byte) 239,
					(byte) 177, (byte) 240, (byte) 174, (byte) 76, (byte) 18, (byte) 145, (byte) 207, (byte) 45,
					(byte) 115, (byte) 202, (byte) 148, (byte) 118, (byte) 40, (byte) 171, (byte) 245, (byte) 23,
					(byte) 73, (byte) 8, (byte) 86, (byte) 180, (byte) 234, (byte) 105, (byte) 55, (byte) 213,
					(byte) 139, (byte) 87, (byte) 9, (byte) 235, (byte) 181, (byte) 54, (byte) 104, (byte) 138,
					(byte) 212, (byte) 149, (byte) 203, (byte) 41, (byte) 119, (byte) 244, (byte) 170, (byte) 72,
					(byte) 22, (byte) 233, (byte) 183, (byte) 85, (byte) 11, (byte) 136, (byte) 214, (byte) 52,
					(byte) 106, (byte) 43, (byte) 117, (byte) 151, (byte) 201, (byte) 74, (byte) 20, (byte) 246,
					(byte) 168, (byte) 116, (byte) 42, (byte) 200, (byte) 150, (byte) 21, (byte) 75, (byte) 169,
					(byte) 247, (byte) 182, (byte) 232, (byte) 10, (byte) 84, (byte) 215, (byte) 137, (byte) 107, 53 };

			/**
			 * 计算数组的CRC8校验值
			 * 
			 * @param data
			 *            需要计算的数组
			 * @return CRC8校验值
			 */
			public static byte genCrc8(byte[] data) {
				return genCrc8(data, 0, data.length, (byte) 0);
			}

			/**
			 * 计算CRC8校验值
			 * 
			 * @param data
			 *            数据
			 * @param offset
			 *            起始位置
			 * @param len
			 *            长度
			 * @return 校验值
			 */
			public static byte genCrc8(byte[] data, int offset, int len) {
				return genCrc8(data, offset, len, (byte) 0);
			}

			/**
			 * 计算CRC8校验值
			 * 
			 * @param data
			 *            数据
			 * @param offset
			 *            起始位置
			 * @param len
			 *            长度
			 * @param preval
			 *            之前的校验值
			 * @return 校验值
			 */
			private static byte genCrc8(byte[] data, int offset, int len, byte preval) {
				byte ret = preval;
				for (int i = offset; i < (offset + len); ++i) {
					ret = crc8_tab[(0x00ff & (ret ^ data[i]))];
				}
				return ret;
			}

			// 测试
			public static void main(String[] args) {
				byte crc = CRC8_Algm1.genCrc8(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
				System.out.println("" + Integer.toHexString(0x00ff & crc));
				assert (Integer.toHexString(0x00ff & crc).equalsIgnoreCase("b6"));
			}
		}

		/*---------------------------------------------------------------------------
		* Copyright (C) 1999,2000 Dallas Semiconductor Corporation, All Rights Reserved.
		*
		* Permission is hereby granted, free of charge, to any person obtaining a
		* copy of this software and associated documentation files (the "Software"),
		* to deal in the Software without restriction, including without limitation
		* the rights to use, copy, modify, merge, publish, distribute, sublicense,
		* and/or sell copies of the Software, and to permit persons to whom the
		* Software is furnished to do so, subject to the following conditions:
		*
		* The above copyright notice and this permission notice shall be included
		* in all copies or substantial portions of the Software.
		*
		* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
		* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
		* MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
		* IN NO EVENT SHALL DALLAS SEMICONDUCTOR BE LIABLE FOR ANY CLAIM, DAMAGES
		* OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
		* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
		* OTHER DEALINGS IN THE SOFTWARE.
		*
		* Except as contained in this notice, the name of Dallas Semiconductor
		* shall not be used except as stated in the Dallas Semiconductor
		* Branding Policy.
		*---------------------------------------------------------------------------
		*/

		/**
		 * CRC8 is a class to contain an implementation of the
		 * Cyclic-Redundency-Check CRC8 for the iButton. The CRC8 is used in the
		 * 1-Wire Network address of all iButtons and 1-Wire devices.
		 * <p>
		 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
		 *
		 * @version 0.00, 28 Aug 2000
		 * @author DS
		 *
		 */
		public static class CRC8_Algm2 {
			/**
			 * CRC 8 lookup table
			 */
			private static byte dscrc_table[];

			/*
			 * Create the lookup table
			 */
			static {
				// Translated from the assembly code in iButton Standards, page
				// 129.
				dscrc_table = new byte[256];
				int acc;
				int crc;
				for (int i = 0; i < 256; i++) {
					acc = i;
					crc = 0;

					for (int j = 0; j < 8; j++) {
						if (((acc ^ crc) & 0x01) == 0x01) {
							crc = ((crc ^ 0x18) >> 1) | 0x80;
						} else
							crc = crc >> 1;
						acc = acc >> 1;
					}
					dscrc_table[i] = (byte) crc;
				}
			}

			/**
			 * Private constructor to prevent instantiation.
			 */
			private CRC8_Algm2() {
			}

			/**
			 * Perform the CRC8 on the data element based on the provided seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            data element on which to perform the CRC8
			 * @param seed
			 *            seed the CRC8 with this value
			 * @return CRC8 value
			 */
			public static int genCrc8(int dataToCRC, int seed) {
				return (dscrc_table[(seed ^ dataToCRC) & 0x0FF] & 0x0FF);
			}

			/**
			 * Perform the CRC8 on the data element based on a zero seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            data element on which to perform the CRC8
			 * @return CRC8 value
			 */
			public static int genCrc8(int dataToCRC) {
				return (dscrc_table[dataToCRC & 0x0FF] & 0x0FF);
			}

			/**
			 * Perform the CRC8 on an array of data elements based on a zero
			 * seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            array of data elements on which to perform the CRC8
			 * @return CRC8 value
			 */
			public static int genCrc8(byte dataToCrc[]) {
				return genCrc8(dataToCrc, 0, dataToCrc.length);
			}

			/**
			 * Perform the CRC8 on an array of data elements based on a zero
			 * seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            array of data elements on which to perform the CRC8
			 * @param off
			 *            offset into array
			 * @param len
			 *            length of data to crc
			 * @return CRC8 value
			 */
			public static int genCrc8(byte dataToCrc[], int off, int len) {
				return genCrc8(dataToCrc, off, len, 0);
			}

			/**
			 * Perform the CRC8 on an array of data elements based on the
			 * provided seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            array of data elements on which to perform the CRC8
			 * @param off
			 *            offset into array
			 * @param len
			 *            length of data to crc
			 * @param seed
			 *            seed to use for CRC8
			 * @return CRC8 value
			 */
			private static int genCrc8(byte dataToCrc[], int off, int len, int seed) {

				// loop to do the crc on each data element
				int CRC8 = seed;

				for (int i = 0; i < len; i++)
					CRC8 = dscrc_table[(CRC8 ^ dataToCrc[i + off]) & 0x0FF];

				return (CRC8 & 0x0FF);
			}

			/**
			 * Perform the CRC8 on an array of data elements based on the
			 * provided seed.
			 * <p>
			 * CRC8 is based on the polynomial = X^8 + X^5 + X^4 + 1.
			 *
			 * @param dataToCrc
			 *            array of data elements on which to perform the CRC8
			 * @param seed
			 *            seed to use for CRC8
			 * @return CRC8 value
			 */
			public static int genCrc8(byte dataToCrc[], int seed) {
				return genCrc8(dataToCrc, 0, dataToCrc.length, seed);
			}

			// 测试
			public static void main(String[] args) {
				int crc = CRC8_Algm2.genCrc8(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
				// System.out.println("" + Integer.toHexString(0x00ff & crc));
				System.out.println("" + Integer.toHexString(0x00ff & crc));
				assert (Integer.toHexString(0x00ff & crc).equalsIgnoreCase("b6"));
			}

		}
		
		public static class Crc16_Algm2 {
			
		    private final static int[] table = { 
		            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241, 
		            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440, 
		            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40, 
		            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841, 
		            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40, 
		            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41, 
		            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641, 
		            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040, 
		            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240, 
		            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441, 
		            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41, 
		            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840, 
		            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41, 
		            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40, 
		            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640, 
		            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041, 
		            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240, 
		            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441, 
		            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41, 
		            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840, 
		            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41, 
		            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40, 
		            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640, 
		            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041, 
		            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241, 
		            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440, 
		            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40, 
		            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841, 
		            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40, 
		            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41, 
		            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641, 
		            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040, 
		        }; 
			public static int genCrc16(byte[] bytes) { 
				int crc = 0x0000; 
		        for (byte b : bytes) { 
		            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff]; 
		        } 
		        return crc; 
		    }   
			
			public static void main(String[] args){
				byte[] arr = new byte[]{0x4, 0x0, 0x1};
				System.out.println(Integer.toString(Crc16_Algm2.genCrc16(arr),16));
				arr = new byte[]{0xB, 0x0, 0x1, 0x1, 0x1, 0x4, (byte) 0xEE, 0x35, 0x45, 0x45 };
				System.out.println(Integer.toString(Crc16_Algm2.genCrc16(arr),16));
			}
		}
		
		
		public static class Crc16_Algm1 {
		     
		    private static final int polynomial = 0x8408;	     
		    private static int[] table = new int[256];
		     
		    public static int genCrc16(int[] bytes) {
		        int crc = 0xffff;
		        for (int i = 0; i < bytes.length; ++i) {
		            int index = (crc ^ bytes[i]) % 256;
		            crc = (crc >> 8) ^ table[index];
		        }
		        return crc;
		    }
		     
		    //init table staticly for compute CRC
		    static {
		        int value;
		        int temp;
		        for (int i = 0; i < table.length; ++i) {
		            value = 0;
		            temp = i;
		            for (byte j = 0; j < 8; ++j) {
		                if (((value ^ temp) & 0x0001) != 0) {
		                    value = (value >> 1) ^ polynomial;
		                } else {
		                    value >>= 1;
		                }
		                temp >>= 1;
		            }
		            table[i] = value;
		        }
		    }
		     
		    public static void main(String[] args) {
		        int[] arr = new int[]{0x4, 0x0, 0x1};
		        System.out.println(Integer.toString(Crc16_Algm1.genCrc16(arr), 16));
		        arr = new int[]{0xB, 0x0, 0x1, 0x1, 0x1, 0x4, 0xEE, 0x35, 0x45, 0x45 };
		        System.out.println(Integer.toString(Crc16_Algm1.genCrc16(arr), 16));
		    }
		}

	}
	
	public static class Thread {

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
		public static String GetThreadName(java.lang.Thread t) {
			return t.currentThread().getName();
		}
	}

	public static class Type {

		public static long bytesToLong(byte[] bytes) {
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.put(bytes, 0, bytes.length);
			buffer.flip();// need flip
			return buffer.getLong();
		}

		// byte 数组与 long 的相互转换
		public static byte[] longToBytes(long x) {
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.putLong(0, x);
			return buffer.array();
		}

	}

}
