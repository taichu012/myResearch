package taichu.research.ai.tag.diskview3.data;

public class TheFile {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSizeKB() {
		return sizeKB;
	}

	public void setSizeKB(long sizeKB) {
		this.sizeKB = sizeKB;
	}

	public double getSizeMB() {
		return sizeMB;
	}

	public void setSizeMB(double sizeMB) {
		this.sizeMB = sizeMB;
	}

	private long sizeKB;

	private double sizeMB;

	private boolean isDir;

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	
	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public boolean isCanExecute() {
		return canExecute;
	}

	public void setCanExecute(boolean canExecute) {
		this.canExecute = canExecute;
	}

	public boolean isCanRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean isCanWrite() {
		return canWrite;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	private String absolutePath;
	private boolean canExecute;
	private boolean canRead;
	private boolean canWrite;
	private boolean isHidden;
	private long lastModifitedTime;

	public long getLastModifitedTime() {
		return lastModifitedTime;
	}

	public void setLastModifitedTime(long lastModifitedTime) {
		this.lastModifitedTime = lastModifitedTime;
	}
	
}
