package taichu.research.ai.tag.diskview3.control;

import java.io.File;

import taichu.research.ai.tag.diskview3.data.TheFile;

public class TheFileHelper {

	private static TheFileHelper instance = null;

	public static TheFileHelper getInstance() {
		if (instance == null) {
			// init;
			return new TheFileHelper();
		} else {
			return instance;
		}
	}
	
	// ����OS���ļ�files����߼�����TheFile
	public static TheFile GenTheFile(File f) {
			TheFile oneFile = new TheFile();
			oneFile.setDir(f.isDirectory());
			oneFile.setName(f.getName());
			oneFile.setSizeKB(f.length() / 1024l);
			oneFile.setSizeMB(f.length() / 1024l / 1024l);
			oneFile.setAbsolutePath(f.getAbsolutePath());
			oneFile.setCanExecute(f.canExecute());
			oneFile.setCanRead(f.canRead());
			oneFile.setCanWrite(f.canWrite());
			oneFile.setHidden(f.isHidden());
			oneFile.setLastModifitedTime(f.lastModified());
			return oneFile;
		}
}
