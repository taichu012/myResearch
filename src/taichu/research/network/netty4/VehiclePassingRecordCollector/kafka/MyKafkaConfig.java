/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.kafka;

import taichu.research.network.netty4.VehiclePassingRecordCollector.Conf;
import taichu.research.tool.IniReader;

/**
 * @author taihcu
 *
 */
public interface MyKafkaConfig {
	
	static final String INI_FILENAME=Conf.getIniPathRelative();
	final static IniReader ini= new IniReader(INI_FILENAME);
	static final String KAFKA_SECTION="kafka.config";
	
//	    final static String zkConnect = "localhost:2181";  
//	    final static String groupId = "testgroup1";  
//	    final static String kafkaServerURL = "localhost";  
//	    final static int kafkaServerPort = 9092;  
//	    final static int kafkaProducerBufferSize = 64 * 1024;  
//	    final static int connectionTimeOut = 20000;  
//	    final static int reconnectInterval = 10000; 
//	    final static String topic1 = "testtopic1"; 
//	    final static String topic2 = "testtopic2";  
//	    final static String topic3 = "testtopic3";  
//	    final static String clientId = "MySimpleConsumerClient";  
	
	final static String groupId = ini.getValue(KAFKA_SECTION, "kafka.consumer.group.id");
    final static String topic1 = ini.getValue(KAFKA_SECTION, "topic1"); 
    final static String topic2 = ini.getValue(KAFKA_SECTION, "topic2");  
    final static String topic3 = ini.getValue(KAFKA_SECTION, "topic3"); 
	   
 

}
