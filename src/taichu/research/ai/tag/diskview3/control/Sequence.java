package taichu.research.ai.tag.diskview3.control;

public class Sequence {
	private static long number=0L;
	
	public static synchronized  long next(){
		number++;
		return number;
	}
	

	public static long getNumber() {
		return number;
	}


	public static synchronized void clear() {
		number=0L;
		
	}
	
	
}
