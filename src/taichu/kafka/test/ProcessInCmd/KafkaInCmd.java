/**
 * 
 */
package taichu.kafka.test.ProcessInCmd;

import taichu.kafka.tool.IniReader;

/**
 * @author taichu
 *
 */
public class KafkaInCmd extends ServerInCmd {

	public static String INI_FILENAME = "D:\\eclipse-workspace\\KafkaTest\\src\\taichu\\kafka\\test\\MyKafkaDemo.ini";

	/**
	 * @param command
	 */
	public KafkaInCmd(String command) {
		super(command);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		IniReader inireader = new IniReader(INI_FILENAME);
		String cmdStartKafka = inireader.getValue("StartServer","cmd.start.kafka");
		
		ZookeeperInCmd kfkcmd = new ZookeeperInCmd(cmdStartKafka);
		Thread p = new Thread(kfkcmd);
		p.start();
	}

}
