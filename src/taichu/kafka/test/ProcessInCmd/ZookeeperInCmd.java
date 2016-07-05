/**
 * 
 */
package taichu.kafka.test.ProcessInCmd;

import taichu.kafka.tool.IniReader;

/**
 * @author taichu
 *
 */
public class ZookeeperInCmd extends ServerInCmd {

	public static String INI_FILENAME = "D:\\eclipse-workspace\\KafkaTest\\src\\taichu\\kafka\\test\\MyKafkaDemo.ini";

	/**
	 * @param command
	 */
	public ZookeeperInCmd(String command) {
		super(command);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		IniReader inireader = new IniReader(INI_FILENAME);
		String cmdStartZk = inireader.getValue("StartServer","cmd.start.zookeepter");
		
		ZookeeperInCmd zkpcmd = new ZookeeperInCmd(cmdStartZk);
		Thread p = new Thread(zkpcmd);
		p.start();
	}

}
