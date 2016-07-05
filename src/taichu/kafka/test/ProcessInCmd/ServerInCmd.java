/**
 * NOTICE!!!
 * 这个class尚未完成，他企图通过线程的控制手段来处理一个cmd独立进程，而cmd独立进程还启动一个java进程。
 * 这样的控制处理链似乎无法处理，所以此基类尚待研究！
 */
package taichu.kafka.test.ProcessInCmd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import kafka.security.auth.Write;
import taichu.kafka.test.KafkaTest.IExitHandling;
import taichu.kafka.tool.F;
import taichu.kafka.tool.IniReader;

/**
 * @author taichu
 *
 */
public class ServerInCmd implements Runnable, IExitHandling {

	private String CmdToStartServer = "";
	private OutputStreamWriter w = null;
	private Scanner s = null;
	public final static String EXIT_CMD = "ABC";
	public volatile boolean exitFlag = false;

	/**
	 * 
	 */
	public ServerInCmd(String command) {
		CmdToStartServer = command;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ServerInCmd s = new ServerInCmd("cmd");
		Thread p = new Thread(s);
		p.start();

		// s = new ServerInCmd(cmdStartZk);
		// p= new Thread(s);
		// p.start();

	}

	public void Stop() {
		boolean result = Stop(EXIT_CMD);

		if (result) {
			System.out.println("Send CMD successful!");
		} else {
			System.out.println("Send CMD failed!");
		}
	}

	private boolean Stop(String cmd) {
		try {
			w.write(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			w.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

	@Override
	public void run() {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(CmdToStartServer);
			System.out.println("Try to start cmd/process [" + CmdToStartServer + "].");

			// set input for process and display in console.
			BufferedInputStream buf = new BufferedInputStream(p.getInputStream());
			s = new Scanner(buf);
			while (s.hasNextLine()) {
				System.out.println(s.nextLine());
			}

			// get output handle for future operate(send Ctrl+C or Exit keys)
			// for process.
			OutputStream is = p.getOutputStream();
			w = new OutputStreamWriter(is);

			p.waitFor();
			System.out.println("Cmd/app [" + CmdToStartServer + "] is closed and then we exit!");
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (p != null) {
				p.destroy();
				System.out.println("Process [PID=" + F.GetF().GetPIDWithDomain() + "] is destoried!");
			}
			System.out.println("If goes here, thread-[" + Thread.currentThread().getName() + "|"
					+ Thread.currentThread().getId() + "] stopped after his son(process) is finished.");
		}
	}

	public void finalize() {
		System.out.println("consumer: Got finalized event, before it do something first!");
		closeall();
	}

	@Override
	public void ExitHandle() {
		System.out.println("Try to stop consumer, wait 2s...");
		// wait time for normally thread stopping by set flag!
		exitFlag = true;
	}

	private void closeall() {
		if (s != null) {
			s.close();
		} else {
			s = null;
		}
		if (w != null) {
			try {
				w.flush();
				w.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			w = null;
		}

	}

}
