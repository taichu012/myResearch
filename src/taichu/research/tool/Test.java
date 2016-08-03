/**
 * 
 */
package taichu.research.tool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ya
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	//@FunctionalInterface
	 interface Converter<F, T> {
	     T convert(F from); 
	 }
	
	public static void test(){
	Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
	 Integer converted = converter.convert("123");
	 System.out.println(converted);    // 123
	 
	 List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
	Collections.sort(names, (a, b) -> b.compareTo(a));
	}
	

	
}
