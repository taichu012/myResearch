/**
 * 
 */
package taichu.kafka.test.KafkaTest;

import java.util.Properties;

//OLD method！Cannot used
//import kafka.javaapi.producer.Producer;
//import kafka.producer.KeyedMessage;  
//import kafka.producer.ProducerConfig;  

import org.apache.kafka.clients.producer.*;
import org.apache.log4j.*;

/******************************************************************************* 
 * MyKafkaProducer.java Created on 2016-06-25 
 * Author: taichu
 * @Title: MyKafkaProducer.java 
 * @Package taichu.kafka
 * Description: 
 * Version: 0.0.1 
******************************************************************************/

/**
 * @author taichu
 *
 */
public class MyKafkaProducer implements Runnable, IExitHandling {

	private static Logger log = Logger.getLogger("MyKafkaProducer.class");
	private static volatile MyKafkaProducer instance = null;
	private KafkaProducer<String, String> producer = null;
	public volatile boolean exitFlag = false; 

	private MyKafkaProducer() {

		Properties props = new Properties();
		// 定义连接的broker list
		props.put("bootstrap.servers", "localhost:9092"); 
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		// 定义序列化类（Java对象的序列化和反序列化）
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		try {
			producer = new KafkaProducer<String, String>(props);
		} catch (Exception e) {
//			System.out.println("Got exception: " + e.toString());
			log.error("Got exception: " + e.toString());
		}
	}

	public static MyKafkaProducer getInstance() {
		if (instance == null) {
			instance = new MyKafkaProducer();
		}
		log.debug("producer：初始化实例返回给caller！");
		return instance;
	}

	public static void main(String[] args) {

		//测试步骤：
		//请先开启zookeeper,kafka,以及一个consumer订阅相同topic！
		//然后运行这个main启动producer线程，会从consumer中看到message被接受！
		
		// 定义topic
		String topic = MyKafkaConfig.topic1;
		// 定义要发送给topic的消息
		String msg = "this is a message消息 to broker!";
		// 构建消息对象
		ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, msg);
		// 推送消息到broker
		MyKafkaProducer mykp=MyKafkaProducer.getInstance();
		mykp.producer.send(data);
		log.debug("Producer: msg=["+msg+"] is sent.");
//		System.out.println("Producer: msg=["+msg+"] is sent.");
		mykp.producer.close();
		
		//如果要测试如下线程，考虑到eclipse的调试结束button是突然中断（突然死亡），对kafka服务器和producer线程不好，
		//建议不用在此main方法中调测线程，而在其他test/demo类中专门调测producer和consumer线程。
		//如果一定要调测，请先开启zookeeper,kafka,以及一个consumer订阅相同topic！
		//然后运行这个main启动producer线程，会从consumer中源源不断的收到message！
		//最后可以用eclipse的突然死亡法中断线程调测；
//		MyKafkaProducer myp = new MyKafkaProducer();
//		Thread  t= new Thread(myp);
//		t.start();

	}

	
	@Override
	public void run() {
		int Nbr = 1;
		KafkaProducer<String, String> p = MyKafkaProducer.getInstance().producer;
		if (p==null) {
			log.error("producer: thread started error!");
			return;
		}
		
		//while (Nbr<=50){
		while (!exitFlag) {
			// 定义消息
			String msg = new String("MessageTo_Broker_消息MSG_" + Nbr);
			// 构建消息对象
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(MyKafkaConfig.topic1, msg);
			// 推送消息到broker
			p.send(data);
			log.debug("Producer: msg=["+msg+"] is sent.");
//			System.out.println("Producer: msg=["+msg+"] is sent.");

			Nbr++;
			try {
				log.info("Producer: sleep 2s...");
//				System.out.println("Producer: sleep 2s...");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				if (p != null)
					p.close();
				p = null;
				return;
			} finally {
			}
		}
	}

	 public void finalize() {
		 log.info("producer: Got finalized event, before it do something first!");
//		 System.out.println("producer: Got finalized event, before it do something first!"); 
		 if (producer!=null){
			 producer.close();
		 }else {
			 producer=null;
		 }
	}
	 
	@Override
	public void ExitHandle() {
		log.info("Producer:Try to stop producer, wait 2s...");
//		System.out.println("Producer:Try to stop producer, wait 2s...");
		//wait time for normally thread stopping by set flag!
		exitFlag = true;
	}


}
