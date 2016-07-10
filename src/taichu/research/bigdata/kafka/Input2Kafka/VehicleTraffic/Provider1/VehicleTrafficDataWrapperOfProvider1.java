/**
 * 
 */
package taichu.research.bigdata.kafka.Input2Kafka.VehicleTraffic.Provider1;

import taichu.research.bigdata.kafka.Input2Kafka.common.IDataTransform2KafkaMsg;

/**
 * @author Administrator
 *
 */
public class VehicleTrafficDataWrapperOfProvider1 implements IDataTransform2KafkaMsg {

	/**
	 * 
	 */
	public VehicleTrafficDataWrapperOfProvider1() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see taichu.research.bigdata.kafka.Input2Kafka.common.IDataTransform2KafkaMsg#ToKafkaMsg(java.lang.String)
	 */
	@Override
	public String ToKafkaMsg(String msg) {
		//return without changed for testing
		return msg;
	}
	
}
