/**
 * 
 */
package taichu.kafka.test.BlockingSocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * 功能说明：执行具体业务的线程
 */
public class DataReceiver implements Runnable {

	// http://www.cnblogs.com/fbsk/archive/2012/02/03/2336689.html

	private static Logger log = Logger.getLogger("DataReceiver.class");
	private Socket socket;
	private BufferedReader is;
	private PrintWriter os;

	private String getTheadName(){
		return Thread.currentThread().getName();
	}
	
	private String getClientInetAdd(){
		return socket.getInetAddress().toString();
	}

	public DataReceiver(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			log.info("DataReceiver: thread("+Thread.currentThread().getName()+") started.");
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());

			String line = is.readLine();
//			log.debug(getTheadName()+": Got MSG(" + line + ") by client-" + getClientInetAdd());
			StringBuilder clientMsg=new StringBuilder();
			clientMsg.append(line);
			//socket.accept线程会因为client没有发送换行回车而阻塞；
			//也会因为最后没有获得约定的结束标记而永远阻塞；
			//如果为socket设定了accept的timeout，则会收到SocketTimeoutException而供你处置，
			//如果你不主动结束socket，即使抛出超时异常，socket依然阻塞！
			//windows下是先回车后换行（\r\n，或13,10，在jmeter里面可以设定E0L=0D0A），MAC OS是回车，Linux是换行
			while (line!=null){
				if ("EOF=XXX".equals(line)){
					log.info(getTheadName()+": Got EOF flag and jump out of recive/read procedure!");
					break;
				}
				line=is.readLine();
				clientMsg.append(line);
//				log.debug(getTheadName()+": Got MSG(" + line + ") by client-" + getClientInetAdd());
			}
			

			log.debug(getTheadName()+":Got TOTAL MSG(" + clientMsg.toString() + ") by client-" + getClientInetAdd());
			// 返回消息给客户端
			String responseMsg = "DataReceiver:got client MSG(" + clientMsg.toString() + ").";
			os.println(responseMsg);
			os.flush();

//			// 对msg消息进行转换
//			String kafkaMsg = dt2km.ToKafkaMsg(msg);
//			if (kafkaMsg != null && "".equals(kafkaMsg)) {
//				// 对消息以producer方式发送给kafka系统
//				boolean ret = kfkp.SendMsg2KafkaAsProducer(kafkaMsg);
//				if (!ret) {
//					log.warn("DataReceiver: Input msg to kafka failed!");
//				} else {
//					log.debug("DataReceiver: Input msg to kafka sucessful!");
//				}
//			} else {
//				log.warn("DataReceiver: Got empty msg and cannot input to kafka!");
//			}

			// TODO：怎么算一次tcp的消息收发结束？
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (socket != null) {
					socket.close();
				}
				log.info("DataReceiver: thread("+Thread.currentThread().getName()+") exit.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
