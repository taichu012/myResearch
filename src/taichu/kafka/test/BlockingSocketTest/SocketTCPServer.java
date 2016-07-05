/**
 * 
 */
package taichu.kafka.test.BlockingSocketTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 */
public class SocketTCPServer {

	private static Logger log = Logger.getLogger("SocketTCPServer.class");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test to start server
		new SocketTCPServer().service();
	}

	private int port = 9923;
	private ServerSocket serverSocket;
	private ExecutorService executorService;// 线程池
	private final int POOL_SIZE = 6;// 单个CPU线程池大小

	public SocketTCPServer() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);// read blocked timeout ms
			// 每个core放入10线程
			int nbrOfCore = Runtime.getRuntime().availableProcessors();
			int nbrOfTotalThreads = nbrOfCore * POOL_SIZE;
			executorService = Executors.newFixedThreadPool(nbrOfTotalThreads);
			log.info("SocketTCPServer：服务器启动,port（" + port + "）,CPU Cores(" + nbrOfCore + ",总线程数(" + nbrOfTotalThreads
					+ ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void service() {
		// System.out.println("socket初始化成功！");
		log.info("SocketTCPServer：socket(tcp)服务端初始化成功！");
		while (true) {
			Socket socket = null;
			try {
				// 接收客户连接,只要客户进行了连接,就会触发accept();从而建立连接
				socket = serverSocket.accept();
				executorService.execute(new DataReceiver(socket));
				// executorService.execute(new Thread());
			} catch (SocketTimeoutException e) {
					//serverSocket.close();
					// 这里不能对socket操作，只能对serverSocket操作；
					// serverSocket=null;
					log.warn("SocketTCPServer：got SocketTimeoutException and stop socket!");
					// log.warn("DataReceiver: got SocketTimeoutException and do
					// nothing!");
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

}
