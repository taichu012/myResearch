/**
 * 
 */
package taichu.kafka.test.KafkaTest;

/**
 * @author taichu
 *
 */
public class MyExitHandler implements Runnable {
	
	private IExitHandling eh=null;

	/**
	 * 
	 */
	public MyExitHandler(IExitHandling ehdr) {
		eh=ehdr;
	}

	@Override
	public void run() {
		//传入实现了ExitHandling接口的类，调用其方法来执行退出前的妥善处理；
//		System.out.println("MyExitHandler:本线程被激活，准备执行退出前的操作！");
		System.out.println("MyExitHandler:Got caller["+eh.getClass().getName()+"], 执行它的退出前处理！");		
		eh.ExitHandle();
		System.out.println("MyExitHandler:退出前的操作执行完毕！本线程退出。");
	}

}
