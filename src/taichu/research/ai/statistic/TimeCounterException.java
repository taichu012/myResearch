/**
 * 
 */
package taichu.research.ai.statistic;

/**
 * @author zl
 *
 */
public class TimeCounterException extends Exception {
	
	private String EID;
	private String EMSG;
	
	
	public static String WAIT_FOR_END="1001";
	public static String[] EID1001 ={WAIT_FOR_END,"TimeCounter haD setted START TIME and wait for setting END TIME!"};

	public static String WAIT_FOR_START="1002";
	public static String[] EID1002 ={WAIT_FOR_START,"TimeCounter waits for setting START TIME for a NEW counter!"};
	

	private TimeCounterException(String eID, String eMSG) {
		EID = eID;
		EMSG = eMSG;
	}
	
	public TimeCounterException TimeCounterException(String e){
		
		if(e==WAIT_FOR_END){
				return new TimeCounterException(TimeCounterException.EID1001[0],TimeCounterException.EID1001[1]);
				}
		else if(e== WAIT_FOR_START) {
				return new TimeCounterException(TimeCounterException.EID1002[0],TimeCounterException.EID1002[1]);
				}
		else {
			return null;
		}
	}

	/**
	 * 
	 */
	public TimeCounterException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TimeCounterException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TimeCounterException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TimeCounterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public TimeCounterException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
