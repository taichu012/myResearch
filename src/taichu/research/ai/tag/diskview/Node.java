/**
 * 
 */
package taichu.research.ai.tag.diskview;

/**
 * @author chen.chao
 * 
 */
public class Node {
	private String tag;
	private String path;
	private boolean isDir; // dir or file

	public Node(String tag, String path) {
		this.tag = tag;
		this.path = path;
		isDir = false;
	}

	public Node(String tag, String path, boolean isDir) {
		this.tag = tag;
		this.path = path;
		this.isDir = isDir;
	}

	public String toString() {
		return tag;
	}

	public String getPath() {
		return path;
	}

	public String getTag() {
		return tag;
	}

	public boolean IsDir() {
		return isDir;
	}

}
