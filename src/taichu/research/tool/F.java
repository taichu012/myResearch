/**
 * 
 */
package taichu.research.tool;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author taichu
 *
 */
public class F {

	private static F instance =null;
	
	/**
	 * 
	 */
	public F() {
	}

	public static F GetF() {
		if (instance == null) {
			instance = new F();
		}
		return instance;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	public String GetPID(){
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		//System.out.println(name);    
		// get pid from pid@domain   
		return  name.split("@")[0];  
	}
	
	public String GetPIDWithDomain(){
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		//System.out.println(name);    
		// get pid@DOMAIN  
		return  name;  
	}
	
	
	@SuppressWarnings("static-access")
	public String GetThreadName(Thread t){
		return t.currentThread().getName();
	}


	long currentTime = System.currentTimeMillis();

	public String getDateTimeFromCurrentTimeMillis() {
	
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
//		Date date = new Date(System.currentTimeMillis());
//		return (formatter.format(date));
		return getDateTimeFromCurrentTimeMillis(System.currentTimeMillis());
	}
	
	public String getDateTimeFromCurrentTimeMillis(long ms) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
		Date date = new Date(ms);
		return (formatter.format(date));
	}
	
	
	 //byte 数组与 long 的相互转换  
	    public static byte[] longToBytes(long x) { 
	    	ByteBuffer buffer = ByteBuffer.allocate(8);  
	        buffer.putLong(0, x); 
	        return buffer.array(); 
	    } 
	  
	    public static long bytesToLong(byte[] bytes) { 
	    	ByteBuffer buffer = ByteBuffer.allocate(8); 
	        buffer.put(bytes, 0, bytes.length); 
	        buffer.flip();//need flip   
	        return buffer.getLong(); 
	    } 
}
