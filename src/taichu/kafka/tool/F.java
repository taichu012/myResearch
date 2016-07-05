/**
 * 
 */
package taichu.kafka.tool;

import java.lang.management.ManagementFactory;

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
	
	
	public String GetThreadName(Thread t){
		return t.currentThread().getName();
	}


}
