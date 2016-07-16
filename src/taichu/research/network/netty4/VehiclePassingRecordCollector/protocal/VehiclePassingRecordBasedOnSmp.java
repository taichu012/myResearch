/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.protocal;

import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public class VehiclePassingRecordBasedOnSmp extends VehiclePassingRecord implements Smp {

	// 请参考SMP协议

	
	//Detail definition of Record
	//max length of ONE message (TCP/SOCKET MTU一般是1452，我们取小于此最大传输单元）
	//ref (http://blog.csdn.net/mazongqiang/article/details/8171805)
	//其实可能netty自己管控这网卡硬件或OS的buffer大小，上层逻辑一般不处理；
	//TODO：为了提升效率，可进一步针对MTU来做优化；
	public static final int MSG_LINE_MAX_LENGTH = 1200;
	
	public static boolean msgTooLong(String msg) {
		return msg.length()>MSG_LINE_MAX_LENGTH;
	}
	
	public static String genLineFromMsg(VehiclePassingRecord msg){
		return T.getT().reflect.genTwoCsvLineFromBeanAttributesAndValues(msg);
	}


}
