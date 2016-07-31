package taichu.research.ai.tag.diskview3.data;

public class Mode {
	
	public Mode(boolean dISK_VIEW, boolean dISK_SCAN) {
		super();
		DISK_VIEW = dISK_VIEW;
		DISK_SCAN = dISK_SCAN;
	}
	
	public Mode() {
		super();
		DISK_VIEW =false;
		DISK_SCAN =false;
	}

	private boolean DISK_VIEW; 
	
	private boolean DISK_SCAN;

	public boolean isDISK_VIEW() {
		return DISK_VIEW;
	}

	public void setDISK_VIEW(boolean dISK_VIEW) {
		DISK_VIEW = dISK_VIEW;
	}

	public boolean isDISK_SCAN() {
		return DISK_SCAN;
	}

	public void setDISK_SCAN(boolean dISK_SCAN) {
		DISK_SCAN = dISK_SCAN;
	} 

}
