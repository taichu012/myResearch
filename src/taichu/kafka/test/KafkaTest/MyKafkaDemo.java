/**
 * 
 */
package taichu.kafka.test.KafkaTest;

import java.io.IOException;

import org.apache.log4j.Logger;

import taichu.kafka.tool.IniReader;

/**
 * @author taichu
 *
 */
class MyKafkaDemo implements IExitHandling {
	private static Logger log = Logger.getLogger("MyKafkaDemo.class");
	public static MyKafkaProducer pdr = null;
	public static MyKafkaConsumer csr = null;
	public static Process zkp = null;
	public static Process kfk = null;
	//ini filename with path
	public static String INI_FILENAME="D:\\RemoteSource\\git.oschina.net\\MyKafka\\src\\taichu\\kafka\\test\\MyKafkaDemo.ini";

	/**
	 * 
	 */
	public MyKafkaDemo() {
	}

	public static void main(String[] args) {

		// 测试步骤；
		// 1 Make sure kafka(with zookeeper inside) is installed on win7;
		// 2 Make setup right path variable in OS (ref to "WIN7下kafka的安装测试.docx")
		// 3 zookeeper和kafka会以独立的cmd进程启动，会等待几秒钟以便初始化；cmd进程启动子java进程；
		// 4 producer和consumer会以线程启动，不断发送和接收消息，以便演示；
		// 5 需要结束demo测试时，在eclipse/console控制台，按下“ENTER”键后任何输入键，则执行关闭；
		// 6 通过shudownhook，自动调用关闭2个线程和2个进程的程序；但是2个进程是独立CMD进程，还需手动关闭cmd窗口！
		//   并确保子java进程也结束（可以通过jconsole来看java进程是否存在）
		// 7 一般来说zookeeper和kafka的独立cmd进程结束，子进程java结束，则端口2181和9092不会被占用，
		//   但也可用netstat -ano|findstr 2181来查看，如果还有端口占用，则通过jconsole查出java进程号，手动给杀死！
		IniReader inireader = new IniReader(INI_FILENAME);

		// start zookeeper server
		String cmdStartZk = inireader.getValue("StartServer", "cmd.start.zookeepter");
		try {
			zkp = Runtime.getRuntime().exec(cmdStartZk);
			log.info("Start zookeeper...wait 10s...!");
//			System.out.println("Start zookeeper...wait 10s...!");
			
			//Below handle standard inputstream of ZOOKEEPER PROCESS
			//But it needn't because we start process as a indepandant CMD process!
//			BufferedInputStream buf = new BufferedInputStream(zkp.getInputStream());
//			Scanner s = new Scanner(buf);
//			while (s.hasNextLine()) {
//				System.out.println(s.nextLine());
//			}
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Start zookeeper error, exit!");
//			System.out.println("Start zookeeper error, exit!");
			return; //return and exit because start server error, cannot test further.
		}

		// start kafka server
		String cmdStartKafka = inireader.getValue("StartServer", "cmd.start.kafka");
		try {
			kfk = Runtime.getRuntime().exec(cmdStartKafka);
			log.info("Start kafka...wait 30s...!");
//			System.out.println("Start kafka...wait 30s...!");
			//Below handle standard inputstream of ZOOKEEPER PROCESS
			//But it needn't because we start process as a indepandant CMD process!
//			System.out.println("Start kafka start...wait 20s...!");
//			BufferedInputStream buf = new BufferedInputStream(zkp.getInputStream());
//			Scanner s = new Scanner(buf);
//			while (s.hasNextLine()) {
//				System.out.println(s.nextLine());
//			}
			Thread.sleep(30000);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			log.error("Start kafka error, exit!");
//			System.out.println("Start kafka error, exit!");
			return; //return and exit because start server error, cannot test further.
		}

		// start a producer thread and send massage to Kafka brokers continuously.
		pdr = MyKafkaProducer.getInstance();
		Thread p = new Thread(pdr);
		p.start();

		// start a consumer thread and receive massages from Kafka brokers continuously.
		csr = MyKafkaConsumer.getInstance();
		Thread c = new Thread(csr);
		c.start();

		//将正常退出（exit/shutdown/jvm受到用户关闭等event事件）绑定到特殊的处理线程上，执行退出前的妥善操作；
		//而处理线程本身不知道该执行什么，通过实现ExitHandling接口，将退出前需要执行妥善操作的类塞入该处理现场；
		MyExitHandler ehd = new MyExitHandler(new MyKafkaDemo());
		Thread t = new Thread(ehd);
		Runtime.getRuntime().addShutdownHook(t);
		//shutdown的hook也可以单独为每个线程（producer和consumer）挂上，它们都实现了ExitHandling接口！

		// use ENTER to call exit() and call shutdown hooker if setted， because
		// eclipse terminates ALL and dangrous！
		log.info("press ENTER to call System.exit() and run the shutdown routine.");
//		System.out.println("press ENTER to call System.exit() and run the shutdown routine.");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			log.debug(e.getLocalizedMessage());
		}
		System.exit(0);
	}

	@Override
	public void ExitHandle() {
		//正常exit前，做些准备工作
		log.info("MyKafkaDemo:准备执行退出前的操作！");
//		System.out.println("MyKafkaDemo:准备执行退出前的操作！");

		try {
			log.info("MyKafkaDemo:Try to stop producer, wait 2s...");
//			System.out.println("MyKafkaDemo:Try to stop producer, wait 2s...");
			pdr.ExitHandle();
			Thread.sleep(2000);
			log.info("MyKafkaDemo:Try to stop consumer, wait 2s...");
//			System.out.println("MyKafkaDemo:Try to stop consumer, wait 2s...");
			csr.ExitHandle();
			Thread.sleep(2000);
			if (kfk!=null) kfk.destroy();
			log.info("MyKafkaDemo:Try to stop kafka, wait 10s...");
//			System.out.println("MyKafkaDemo:Try to stop kafka, wait 10s...");
			Thread.sleep(10000);
			if (zkp!=null) zkp.destroy();
			log.info("MyKafkaDemo:Try to stop zookeeper, wait 10s...");
//			System.out.println("MyKafkaDemo:Try to stop zookeeper, wait 10s...");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("MyKafkaDemo:退出前的操作退出完成！");
//		System.out.println("MyKafkaDemo:退出前的操作退出完成！");

	}

}
