/**
 * 
 */
package taichu.kafka.Input2Kafka.common;


/**
 * @author Administrator
 *
 */
public interface IKafkaProducer {

	//TODO:补充接口以表达将msg输入kafka的能力，也许是单个线程（多个线程？）
//	private IKafkaProducer<String, String> producer = null;
	
	public boolean SendMsg2KafkaAsProducer(String msg);
	
}
