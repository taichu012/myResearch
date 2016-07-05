/**
 * 
 */
package taichu.kafka.Input2Kafka.common;

/**
 * @author Administrator
 *
 */
public interface IDataTransform2KafkaMsg {

	//transform msg to right format and ready for send to kafka via producer
	public String ToKafkaMsg(String msg);

	
}
