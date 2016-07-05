/**
 * 
 */
package taichu.kafka.test.KafkaTest;

/**
 * @author taihcu
 *
 */
public interface MyKafkaConfig {
	
	    final static String zkConnect = "localhost:2181";  
	    final static String groupId = "testgroup1";  
	    final static String kafkaServerURL = "localhost";  
	    final static int kafkaServerPort = 9092;  
	    final static int kafkaProducerBufferSize = 64 * 1024;  
	    final static int connectionTimeOut = 20000;  
	    final static int reconnectInterval = 10000; 
	    final static String topic1 = "testtopic1"; 
	    final static String topic2 = "testtopic2";  
	    final static String topic3 = "testtopic3";  
	    final static String clientId = "MySimpleConsumerClient";  
 

}
