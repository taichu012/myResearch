package taichu.research.ai.tag.diskview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DiskView implements TreeSelectionListener, TreeWillExpandListener {

	private JFrame frame;
	private JTable table;
	private JTree tree;
	private static boolean DEBUG = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskView window = new DiskView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// SHOW OS INFO
		if (DEBUG) {
			Tool.getInstance().showOSInfo();
		}
		;

		// print DISK FOLDER INFO
		// Vector<String> outFileLists = null;
		// Tool.getInstance().printFolder("C://temp//mam//");
	}

	/**
	 * Create the application.
	 */
	public DiskView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Tag");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		// Test create a basic tree.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("C:\\");
		// createNodes(root);
		tree = new JTree(root);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// �ڵ㱻չ��������ѡ��û�иı䡣.
		tree.addTreeWillExpandListener(this);

		// // Set the icon for leaf nodes.
		// ImageIcon leafIcon = createImageIcon("image/leaf.gif");
		// if (leafIcon != null) {
		// DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		// renderer.setLeafIcon(leafIcon);
		// tree.setCellRenderer(renderer);
		// } else {
		// System.err.println("Leaf icon missing; using default.");
		// }

		JScrollPane aScrollPaneLeft = new JScrollPane();
		aScrollPaneLeft.setBorder(null);
		aScrollPaneLeft.getViewport().add(tree);

		JScrollPane aScrollPaneRight = new JScrollPane();
		aScrollPaneRight.setBorder(null);
		aScrollPaneRight.getViewport().add(table);

		splitPane.setLeftComponent(aScrollPaneLeft);
		// splitPane.setLeftComponent(tree);

		table = new JTable();
		table.setBackground(Color.GRAY);
		splitPane.setRightComponent(aScrollPaneRight);

		// ��TREE��ʾĳ·����
		DisplayPathInTree("C://", false, root);
	}

	private void DisplayPathInTree(String path, boolean stopThisLevel, DefaultMutableTreeNode node) {
		// JTREE���ؼ��ڵ��ͼ���Ǹ����Ƿ���children��ȷ���Ƿ���LEAF�ġ�

		String fileSeperator = Tool.getOSFileSeparator();

		// pathΪ��������ΪĬ�ϡ�C://��
		if (path.length() == 0) {
			path = "C://";
		}

		DefaultMutableTreeNode tempTreeNode = null;
		Node tempNode = null;

		// ��ʼ������Ŀ¼��ʾ��TREE�У��������һ��Ľڵ㣨�ܼ�2�㣬������֧��Ҷ�ӵȴ�����ٴ���
		File file = new File(path);
		if (file.exists()) {
			// If having file OR dir under path, then produce them
			// Otherwise, return
			try {
				File files[] = file.listFiles();
				// û���ӽڵ���ļ��������Ŀ¼����Ҫ���<��>�����ӽڵ㣡
				if (file.isDirectory() && files.length == 0) {
					node.add(new DefaultMutableTreeNode(new Node("<��>", file.getAbsolutePath())));
					return;
				}
				for (int i = 0; i < files.length; i++) {
					// for (int i = files.length-1; i >=0; i--) {
					if (files[i].isFile()) {
						// display leaf/file node
						tempNode = new Node(files[i].getName(), files[i].getAbsolutePath(), false);
						tempTreeNode = new DefaultMutableTreeNode(tempNode);
						node.add(tempTreeNode);
						System.out.println("add LEAF[" + tempTreeNode + "]");
					} else if (files[i].isDirectory()) {
						// display branch/dir node
						tempNode = new Node(files[i].getName(), files[i].getAbsolutePath(), true);
						tempTreeNode = new DefaultMutableTreeNode(tempNode);
						node.add(tempTreeNode);
						System.out.println("add DIR[" + tempTreeNode + "]");
						if (!stopThisLevel) {
							// produce hide/next level of current branch/dir
							// first before it be clicked!
							// Otherwise,leave branch/dir alone, don't produce
							// them further!
							try {
								DisplayPathInTree(path + fileSeperator + files[i].getName(), true, tempTreeNode);
							} catch (Exception e) {
								// ��������"System Volume
								// Information"��Ŀ¼�޷�����ģ�����exception��
								// e.printStackTrace();
								return;
							}
						} else {
							// ֻԤ��2�㣬���ͣ����㣬���������DIR�����TAG='DUMMY'��LEAF�ڵ�
							tempTreeNode.add(new DefaultMutableTreeNode(new Node("DUMMY", files[i].getAbsolutePath())));

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = DiskView.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/*
	 * private void createNodes(DefaultMutableTreeNode top) {
	 * DefaultMutableTreeNode category = null; DefaultMutableTreeNode book =
	 * null;
	 * 
	 * category = new DefaultMutableTreeNode("Books for Java Programmers");
	 * top.add(category);
	 * 
	 * // original Tutorial book = new DefaultMutableTreeNode(new Node(
	 * "The Java Tutorial: A Short Course on the Basics", "tutorial.html"));
	 * category.add(book); }
	 */

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		Node oneNode = (Node) nodeInfo;
		if (!node.isLeaf()) {
			// �����Ŀ¼����չ����

			// ��������һ��DUMMYҶ�ӵ�DIRΪû�б�Ԥ��������������Ķ�������ˣ����Ե���
			DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) node.getLastChild();
			if ("DUMMY" == ((Node) lastChild.getUserObject()).getTag()) {
				// ������DUMMY��DIR��˵����Ҫ��һ����չ������˵���Ѿ�������ˣ�
				node.removeAllChildren(); // remove DUMMY first!

				tree.updateUI();// ������ԭ����ͼ���TREE��չ�����������¼�������޷�ˢ�£�

				if (DEBUG) {
					System.out.println("DIR [" + oneNode.getPath() + "]: is clicked չ���²㣬Ԥ�����²㣡");
				}

				DisplayPathInTree(oneNode.getPath(), false, node);
			} else {
				if (DEBUG) {
					System.out.println("DIR [" + oneNode.getPath() + "]: is clicked�������Ѿ�����������ټ�������");
				}

			}

		} else {
			// �����leaf��������
			if (DEBUG) {
				System.out.println("LEAF [" + oneNode.getPath() + "]: is clicked��");
			}
		}
	}

	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		// ��TREE�ڵ㣬��������˸ýڵ㴦��
		TreePath path = event.getPath();
		tree.setSelectionPath(path);
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		// �۵�tree�ڵ㲻����

	}

}
