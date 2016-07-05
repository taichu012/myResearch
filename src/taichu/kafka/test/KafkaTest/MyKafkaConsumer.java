/**
 * 
 */
package taichu.kafka.test.KafkaTest;

import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.*;
import org.apache.log4j.*;

/**
 * @author taichu
 *
 */
public class MyKafkaConsumer  implements Runnable, IExitHandling {
	
	private static Logger log = Logger.getLogger("MyKafkaConsumer.class");
	private static volatile MyKafkaConsumer instance = null;
	private KafkaConsumer<String, String> consumer = null;
	public static int MAX_NBR_GET_NOTHING=10;
	public volatile boolean exitFlag = false; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//如果要测试如下线程，考虑到eclipse的调试结束button是突然中断（突然死亡），对kafka服务器和producer线程不好，
		//建议不用在此main方法中调测线程，而在其他test/demo类中专门调测producer和consumer线程。
		//如果一定要调测，请先开启zookeeper，kafka，producer，然后运行这个main启动consumer线程。
		//consumer线程有一个设计是没有收到消息的连续尝试次数倒计时，可配置，并会自动结束线程！
		MyKafkaConsumer mykc = MyKafkaConsumer.getInstance();
		Thread t = new Thread(mykc);
		t.start();
	}


	/**
	 * 
	 */
	private MyKafkaConsumer() {

		Properties props = new Properties();
		
		//Below are producer's params, never used for consumer!!!
		// props.put("acks", "all");
		// props.put("retries", 0);
		// props.put("batch.size", 16384);
		// props.put("linger.ms", 1);
		// props.put("buffer.memory", 33554432);
		// props.put("zookeeper.connect", MyKafkaConfig.zkConnect);
		// props.put("zookeeper.session.timeout.ms", "40000");
		// props.put("zookeeper.sync.time.ms", "200");

		// 定义序反列化类（Java对象传输前要序列化）
		props.put("bootstrap.servers", "localhost:9092"); // 定义连接的broker list
	    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("group.id", MyKafkaConfig.groupId);

		try {
			consumer = new KafkaConsumer<String, String>(props);
		} catch (Exception e) {
			log.error("Got exception: " + e.toString());
			//System.out.println("Got exception: " + e.toString());
			log.debug("Got exception: " + e.toString());
		}
	}

	
	public static MyKafkaConsumer getInstance() {
		if (instance == null) {
			instance = new MyKafkaConsumer();
		}
		log.debug("CONSUMER:Got instance and return to caller");
		return instance;
	}
	
	
	@Override
	public void run() {
		int exitCountDown=MAX_NBR_GET_NOTHING;

		KafkaConsumer<String, String> c = MyKafkaConsumer.getInstance().consumer;
		if (c!=null) {
			c.subscribe(Arrays.asList(MyKafkaConfig.topic1, MyKafkaConfig.topic2, MyKafkaConfig.topic3));
			log.debug("CONSUMER:thread started!");
		}else {
			log.error("CONSUMER:thread started error!");
			return;
			
		}

		while (!exitFlag) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			
			//连续没有收到消息就减1，一旦收到消息就重新开始计数
			if (records.count()<=0){
				exitCountDown--; 
			} else{
				exitCountDown=MAX_NBR_GET_NOTHING;
			};
			
			for (ConsumerRecord<String, String> record: records) {
				log.info("Consumer: got msg, offset=["+record.offset()+"],key=["+record.key()+"],val=["+record.value()+"]"+"\n");
//				System.out.printf("Consumer: got msg=[offset = %d, key = %s, value = %s] \n", record.offset(), record.key(), record.value());
			}
			try {
				log.info("Consumer: sleep 5s...");
//				System.out.println("Consumer: sleep 5s...");
				Thread.sleep(5000);
				//连续消息满一定次数就退出；
				if (exitCountDown<=0){
					log.debug("Exit after "+MAX_NBR_GET_NOTHING*5+
							"s get nothing!");
//					System.out.println("Consumer: Exit after "+MAX_NBR_GET_NOTHING*5+
//							"s get nothing!");
					exitFlag=true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getMessage());
				if (c != null)
					c.close();
				c = null;
				return;
			} finally {
			}
		}
	}
	

	 public void finalize() {
		 log.debug("consumer: Got finalized event, before it do something first!");
//		 System.out.println("consumer: Got finalized event, before it do something first!"); 
		 if (consumer!=null){
			 consumer.close();
		 }else {
			 consumer=null;
		 }
	}
	 
	@Override
	public void ExitHandle() {
		log.debug("consumer:Try to stop consumer, wait 2s...");
//		System.out.println("consumer:Try to stop consumer, wait 2s...");
		//wait time for normally thread stopping by set flag!
		exitFlag = true;
	}



}
