/**
 * 
 */
package taichu.research.network.netty4.VehiclePassingRecordCollector.protocal;

import taichu.research.network.netty4.VehiclePassingRecordCollector.entity.VehiclePassingRecord;
import taichu.research.tool.Delimiters;
import taichu.research.tool.T;

/**
 * @author taichu
 *
 */
public class VehiclePassingRecordBasedOnSmp extends VehiclePassingRecord implements Smp {

	// /*
	// 请参考SMP协议
	// */

	
	
	//Detail definition of Record
	//max length of ONE line (TCP/SOCKET MTU一般是1452，我们取小于此最大传输单元）
	//ref (http://blog.csdn.net/mazongqiang/article/details/8171805)
	//其实可能netty自己管控这网卡硬件或OS的buffer大小，详细待查 TODO：
	public static final int MSG_LINE_MAX_LENGTH = 1200;
	
	public static boolean lineLengthBeyondLimitation(String record) {
		return record.length()>MSG_LINE_MAX_LENGTH;
	}
	
	public static String genOneMessage(VehiclePassingRecord record){
		return T.getT().reflect.OutputEntityFieldsAsCsvLine(record);
	}


}
