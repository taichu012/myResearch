package taichu.research.ai.tag.diskview;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

public final class Tool {

	private static Tool instance = null;
	private static String fileSeperator = null;

	public static Tool getInstance() {
		if (instance == null) {
			// init;
			fileSeperator = getOSFileSeparator();

			return new Tool();
		} else {
			return instance;
		}
	}

	// 不保证功能完好，仅用来显示相关语法和语句的使用
	public boolean getFileList(Vector<String> outFileLists, String filePath,
			boolean subFolderFlag) {
		if (outFileLists == null) {
			outFileLists = new Vector<String>();
		}
		File file = new File(filePath);
		if (file.exists()) {
			File files[] = file.listFiles();
			if (subFolderFlag) {
				for (int i = 0; i < files.length; i++) {
					// for (int i = files.length-1; i >=0; i--) {
					if (files[i].isFile()) {
						outFileLists.add(files[i].getName());
						System.out.println(files[i].getName());
					} else if (files[i].isDirectory()) {
						outFileLists
								.add("目录 [" + files[i].getName() + "]-----");
						System.out.println("目录 [" + files[i].getName()
								+ "]-----");
						getFileList(outFileLists, filePath + fileSeperator
								+ files[i].getName(), subFolderFlag);
					}
				}
			} else {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						outFileLists.add(files[i].getName());
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	// 不保证功能完好，仅用来显示相关语法和语句的使用
	public void printFolder(String rootPath) {
		Vector<String> files = new Vector<String>();
		// getFileList(files, "C://", true);
		getFileList(files, rootPath, true);
		// for (String string : files) {
		// System.out.println(string);
		// }
	}

	public static String getOSFileSeparator() {
		Properties props = System.getProperties(); // 获得系统属性集
		return props.getProperty("file.separator"); // 获取OS的目录价分隔符
	}

	public void showOSInfo() {
		Properties props = System.getProperties(); // 获得系统属性集
		String osName = props.getProperty("os.name"); // 操作系统名称
		String osArch = props.getProperty("os.arch"); // 操作系统构架
		String osVersion = props.getProperty("os.version"); // 操作系统版本
		String fileSeparator = props.getProperty("file.separator"); // 获取OS的目录价分隔符

		System.out.println("os.name=" + osName);
		System.out.println("os.arch=" + osArch);
		System.out.println("os.version" + osVersion);
		System.out.println("file.separator" + fileSeparator);

		// /*public static String getProperty(String key)
		// 键 相关值的描述
		// java.version Java 运行时环境版本
		// java.vendor Java 运行时环境供应商
		// java.vendor.url Java 供应商的 URL
		// java.home Java 安装目录
		// java.vm.specification.version Java 虚拟机规范版本
		// java.vm.specification.vendor Java 虚拟机规范供应商
		// java.vm.specification.name Java 虚拟机规范名称
		// java.vm.version Java 虚拟机实现版本
		// java.vm.vendor Java 虚拟机实现供应商
		// java.vm.name Java 虚拟机实现名称
		// java.specification.version Java 运行时环境规范版本
		// java.specification.vendor Java 运行时环境规范供应商
		// java.specification.name Java 运行时环境规范名称
		// java.class.version Java 类格式版本号
		// java.class.path Java 类路径
		// java.library.path 加载库时搜索的路径列表
		// java.io.tmpdir 默认的临时文件路径
		// java.compiler 要使用的 JIT 编译器的名称
		// java.ext.dirs 一个或多个扩展目录的路径
		// os.name 操作系统的名称
		// os.arch 操作系统的架构
		// os.version 操作系统的版本
		// file.separator 文件分隔符（在 UNIX 系统中是“/”）
		// path.separator 路径分隔符（在 UNIX 系统中是“:”）
		// line.separator 行分隔符（在 UNIX 系统中是“/n”）
		// user.name 用户的账户名称
		// user.home 用户的主目录
		// user.dir 用户的当前工作目录 */
	}

}
