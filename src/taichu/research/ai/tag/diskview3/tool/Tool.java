package taichu.research.ai.tag.diskview3.tool;

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

	// ����֤������ã���������ʾ����﷨������ʹ��
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
								.add("Ŀ¼ [" + files[i].getName() + "]-----");
						System.out.println("Ŀ¼ [" + files[i].getName()
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

	// ����֤������ã���������ʾ����﷨������ʹ��
	public void printFolder(String rootPath) {
		Vector<String> files = new Vector<String>();
		// getFileList(files, "C://", true);
		getFileList(files, rootPath, true);
		// for (String string : files) {
		// System.out.println(string);
		// }
	}

	public static String getOSFileSeparator() {
		Properties props = System.getProperties(); // ���ϵͳ���Լ�
		return props.getProperty("file.separator"); // ��ȡOS��Ŀ¼�۷ָ���
	}

	public static String getUserDir() {
		Properties props = System.getProperties(); // ���ϵͳ���Լ�
		return props.getProperty("user.dir"); //��ȡ�û���ǰĿ¼
	}

	
	public void showOSInfo() {
		Properties props = System.getProperties(); // ���ϵͳ���Լ�
		String osName = props.getProperty("os.name"); // ����ϵͳ����
		String osArch = props.getProperty("os.arch"); // ����ϵͳ����
		String osVersion = props.getProperty("os.version"); // ����ϵͳ�汾
		String fileSeparator = props.getProperty("file.separator"); // ��ȡOS��Ŀ¼�۷ָ���
		String userDir = props.getProperty("user.dir");//��ȡ�û���ǰĿ¼

		System.out.println("os.name=" + osName);
		System.out.println("os.arch=" + osArch);
		System.out.println("os.version=" + osVersion);
		System.out.println("file.separator=" + fileSeparator);
		System.out.println("user.dir=" + userDir);

		// /*public static String getProperty(String key)
		// �� ���ֵ������
		// java.version Java ����ʱ�����汾
		// java.vendor Java ����ʱ������Ӧ��
		// java.vendor.url Java ��Ӧ�̵� URL
		// java.home Java ��װĿ¼
		// java.vm.specification.version Java ������淶�汾
		// java.vm.specification.vendor Java ������淶��Ӧ��
		// java.vm.specification.name Java ������淶����
		// java.vm.version Java �����ʵ�ְ汾
		// java.vm.vendor Java �����ʵ�ֹ�Ӧ��
		// java.vm.name Java �����ʵ������
		// java.specification.version Java ����ʱ�����淶�汾
		// java.specification.vendor Java ����ʱ�����淶��Ӧ��
		// java.specification.name Java ����ʱ�����淶����
		// java.class.version Java ���ʽ�汾��
		// java.class.path Java ��·��
		// java.library.path ���ؿ�ʱ������·���б�
		// java.io.tmpdir Ĭ�ϵ���ʱ�ļ�·��
		// java.compiler Ҫʹ�õ� JIT ������������
		// java.ext.dirs һ��������չĿ¼��·��
		// os.name ����ϵͳ������
		// os.arch ����ϵͳ�ļܹ�
		// os.version ����ϵͳ�İ汾
		// file.separator �ļ��ָ������� UNIX ϵͳ���ǡ�/����
		// path.separator ·���ָ������� UNIX ϵͳ���ǡ�:����
		// line.separator �зָ������� UNIX ϵͳ���ǡ�/n����
		// user.name �û����˻�����
		// user.home �û�����Ŀ¼
		// user.dir �û��ĵ�ǰ����Ŀ¼ */
	}

}
