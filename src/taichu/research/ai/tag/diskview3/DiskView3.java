package taichu.research.ai.tag.diskview3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;

import taichu.research.ai.tag.diskview3.control.Sequence;
import taichu.research.ai.tag.diskview3.control.TheFileHelper;
import taichu.research.ai.tag.diskview3.data.Mode;
import taichu.research.ai.tag.diskview3.data.Node;
import taichu.research.ai.tag.diskview3.data.TheFile;
import taichu.research.ai.tag.diskview3.jna.Kernel32;
import taichu.research.ai.tag.diskview3.tool.Tool;

public class DiskView3 implements TreeSelectionListener, TreeWillExpandListener {

	private JFrame frame;
	private JTable table;
	private JTree tree;
	private JTabbedPane tabpane;
	private JTextPane txtpane;
	private JLabel label;
	private JToolBar toolBar;
	// private JFreeChart chart;
	private ChartPanel chartPanel;
	private static boolean DEBUG = true;
	private boolean ISTREEOPEN = false;
	// private static final String ROOTPATH_4_DISKVIEW = "C:\\";
	private static final String ROOTPATH_4_DISKVIEW = "C:\\dzh\\";
	private static final String ROOTPATH_4_DISKSCAN = "C:\\dzh\\";
	private static final int MAX_DEPT = 10;

	private Mode mode = new Mode(false, false);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskView3 window = new DiskView3();
					window.frame.pack();
					RefineryUtilities.centerFrameOnScreen(window.frame);
					// RefineryUtilities.centerDialogInParent((Dialog)window.frame);
					window.frame.setVisible(true);
					RefineryUtilities.centerFrameOnScreen(window.frame);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// SHOW OS INFO
		if (DEBUG) {
			Tool.showOSInfo();
			// System.out.println(Tool.getSystemTimeByWin32());

		}
		;
	}

	/**
	 * Create the application.
	 */
	public DiskView3() {
		Init();
	}

	private void PrepareExistTree4DiskView(JTree tree, String rootPath) {
		// Test create a basic tree.

		Node rootNode = new Node(rootPath, rootPath, true);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree.getModel()
				.getRoot());
		root.setUserObject(rootNode);

		// 用默认的图标（文件夹图标）设置为leaf图标，这样文件夹和文件图标都一样
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(renderer.getDefaultClosedIcon());
		tree.setCellRenderer(renderer);

		GenData4Tree(root);
		RefreshUI();
	}

	private void PrepareExistTree4DiskScan(JTree tree, String rootPath) {
		// Test create a basic tree.

		Node rootNode = new Node(rootPath, rootPath, true);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree.getModel()
				.getRoot());
		root.setUserObject(rootNode);

		// 用默认的图标（文件夹图标）设置为leaf图标，这样文件夹和文件图标都一样
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(renderer.getDefaultClosedIcon());
		tree.setCellRenderer(renderer);

		GenData4TreeRecursion(root, MAX_DEPT);
		RefreshUI();
	}

	private void DoDiskScan() {
		// scan root path, appennd all dir to tree and all dir&file to
		// table

		// 设置排他“DISKSCAN”模式
		mode.setDISK_SCAN(true);
		mode.setDISK_VIEW(false);

		// 处理TABLE控件，清空
		RemoveAllRows4Table(table);
		Sequence.clear();

		// 处理TREE控件，为null则第一次处理，非null则第n次处理，清空
		if (tree == null) {
			tree = InitTree4DiskScan(ROOTPATH_4_DISKSCAN);
		} else {
			// 如果tree不是第一次建立，之后都仅是清楚节点并重新放置节点，不能重建全新的JTree控件！！！
			RemoveAllChildren4Tree(tree);
			PrepareExistTree4DiskScan(tree, ROOTPATH_4_DISKSCAN);
		}
		RefreshUI();
	}

	private void DoDiskView() {
		// 设置排他“diskview”模式
		mode.setDISK_SCAN(false);
		mode.setDISK_VIEW(true);

		// 处理TABLE控件，清空
		RemoveAllRows4Table(table);

		// 处理TREE控件，为null则第一次处理，非null则第n次处理，清空
		if (tree == null) {
			tree = InitTree4DiskView(ROOTPATH_4_DISKVIEW);
		} else {
			// 如果tree不是第一次建立，之后都仅是清楚节点并重新放置节点，不能重建全新的JTree控件！！！
			RemoveAllChildren4Tree(tree);
			PrepareExistTree4DiskView(tree, ROOTPATH_4_DISKVIEW);
			this.expandRoot(tree);
		}
		RefreshUI();

		// Node tempNode = new Node(rootPath, rootPath, true);
		// DefaultMutableTreeNode root = new DefaultMutableTreeNode(tempNode);
		// DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootPath);
		// 用TREE僅僅显示目錄DIR
		// GenData4Tree("C://", root);
		// GenData4Tree(rootPath,root);
		// GenData4Tree(root);
		// Mode mode = new Mode(false, false);
		// GenData4Tree((DefaultMutableTreeNode) tree.getModel().getRoot());
	}
	
	private void expandRoot(JTree tree){
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree
				.getModel().getRoot());
		tree.expandPath(new TreePath(root));
		RefreshUI();
	}

	
	private void collapseRoot(JTree tree){
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree
				.getModel().getRoot());
		tree.collapsePath(new TreePath(root));
	}
	
	private void expandOrCollapseAll(JTree tree, TreePath parent, boolean expandOrCollapse) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandOrCollapseAll(tree, path, expandOrCollapse);
			}
		}
		if (expandOrCollapse) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
	
	private void expandAll(JTree tree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree
				.getModel().getRoot());
		TreePath parent = new TreePath(root);
		this.expandOrCollapseAll(tree, parent,true);
	}

	private JToolBar InitToolBar() {
		toolBar = new JToolBar();
		JButton jbtExit = new JButton("Exit");
		JButton jbtDiskScan = new JButton("DiskScan");
		JButton jbtDiskView = new JButton("DiskView");
		JButton jbtTest = new JButton("展开树");

		jbtExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		jbtDiskScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// scan root path, appennd all dir to tree and all dir&file to
				// table
				DoDiskScan();
			}
		});
		jbtDiskView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DoDiskView();
			}
		});
		jbtTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 清理TREE控件，重新做disk view。
				 test();
			}
		});

		toolBar.add(jbtExit);
		toolBar.add(jbtDiskScan);
		toolBar.add(jbtDiskView);
		toolBar.add(jbtTest);
		return toolBar;
	}

	private void test() {
//		// clear tree
//		if (tree != null) {
//			// tree.setModel(null);
//			RemoveAllChildren4Tree(tree);
//			tree.updateUI();// 可以更新画面！
//			// tree.revalidate();//完全无效！
//			// tree.repaint();//
//			// tree=null; //界面初始化后无法更改tree控件，只能通过事件和修改内容来操作它！
//		}
//
//		// clear table
//		if (table != null) {
//			RemoveAllRows4Table(table);
//		}

		// tree.revalidate();
		// tree.updateUI();
		// tree.repaint();
		// tree=null;
		
		if (ISTREEOPEN){
			//close tree
			this.collapseRoot(tree);
			this.ISTREEOPEN=false;
		}else {
			//this.expandRoot(tree);
			this.expandAll(tree);
			this.ISTREEOPEN=true;
		}
		tree.updateUI();
	}

	private void RemoveAllChildren4Tree(JTree tree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) (tree.getModel()
				.getRoot());
		root.removeAllChildren();
		// root.setUserObject(null);
		RefreshUI();
	}

	private void RefreshUI() {
		// resfresh all UI

		// tree.invalidate();
		tree.updateUI();
		// tree.repaint();
		// table.invalidate();
		// table.updateUI();
		// label.invalidate();
		// label.updateUI();

		// chartPanel.updateUI();
		// txtpane.updateUI();

	}

	private JTree InitTree4DiskView(String rootPath) {
		return InitTree(rootPath);
	}

	private JTree InitTree(String rootPath) {
		// Test create a basic tree.

		Node rootNode = new Node(rootPath, rootPath, true);
		// root.setUserObject(rootNode);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode);

		// createNodes(root);
		JTree newTree = new JTree(root);
		newTree.setShowsRootHandles(true);
		newTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		newTree.addTreeSelectionListener(this);

		// 节点被展开，但是选中没有改变。.
		newTree.addTreeWillExpandListener(this);

		// // Set the icon for leaf nodes.
		// ImageIcon leafIcon = CreateImageIcon("image/leaf.gif");
		// if (leafIcon != null) {
		// DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		// renderer.setLeafIcon(leafIcon);
		// newTree.setCellRenderer(renderer);
		// } else {
		// System.err.println("Leaf icon missing; using default.");
		// }

		// 用默认的图标（文件夹图标）设置为leaf图标，这样文件夹和文件图标都一样
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(renderer.getDefaultClosedIcon());
		newTree.setCellRenderer(renderer);

		return newTree;
	}

	private JTree InitTree4DiskScan(String rootPath) {
		return InitTree(rootPath);
	}

	private JTable InitTable() {
		DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		GenTableCol(model);
		table.setBackground(Color.GRAY);
		// table.updateUI();
		// table.invalidate();
		// table.repaint();
		return table;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void Init() {
		// 設置tool bar
		toolBar = InitToolBar();
		// 設置split panel
		JSplitPane splitPane = new JSplitPane();
		// 設置label
		label = new JLabel("信息:", JLabel.LEFT);
		label.setEnabled(false);

		// 設置東南西北frame結構
		frame = new JFrame("Tag");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.SOUTH);

		// 設置table，必須先於TREE初始化
		table = InitTable();

		// 設置tree并启动DISKVIEW模式
		DoDiskView();

		// 初始化chart
		chartPanel = new ChartPanel(this.MakePieChart4FileSize(
				new DefaultPieDataset(), "当前目录文件SIZE分布", "当前目录文件SIZE分布饼图.png",
				null));

		// //////////////////////////////////////////////////
		// 這裡調測JNA有問題----BEGIN
		// //////////////////////////////////////////////////
		// INIT textpanel
		txtpane = new JTextPane();
		txtpane.setText(Tool.getSystemTimeByWin32());
		Kernel32.FILETIME fileCreateTime = new Kernel32.FILETIME();
		Kernel32.FILETIME fileAccessTime = new Kernel32.FILETIME();
		Kernel32.FILETIME fileWriteTime = new Kernel32.FILETIME();

		// "C:\\"能允许dialog，但无数据
		// txtpane.setText(Tool.getFileTimeByWin32(Tool.openFilebyWin32("C:\\"),fileCreateTime,
		// fileAccessTime, fileWriteTime));
		// "C:\\temp\\"引起错误。
		// txtpane.setText(Tool.getFileTimeByWin32(Tool.openFilebyWin32("C:\\temp\\"),fileCreateTime,
		// fileAccessTime, fileWriteTime));
		// "C:\\"无错误，不出现dialog
		// txtpane.setText(Tool.getFileTimeByWin32(Tool.openFilebyWin32("C:\\temp\\ocx.txt"),fileCreateTime,
		// fileAccessTime, fileWriteTime));
		System.out.println("testOK?");
		// //////////////////////////////////////////////////
		// 這裡調測JNA有問題----BEGIN
		// //////////////////////////////////////////////////

		// 設置滾動條，tab，并放入split
		JScrollPane aScrollPane4Text = new JScrollPane();
		aScrollPane4Text.setBorder(null);
		aScrollPane4Text.setViewportView(txtpane);

		JScrollPane aScrollPanel4ChartPanel = new JScrollPane();
		aScrollPanel4ChartPanel.setBorder(null);
		aScrollPanel4ChartPanel.setViewportView(chartPanel);

		JScrollPane aScrollPane4Tree = new JScrollPane();
		aScrollPane4Tree.setBorder(null);
		aScrollPane4Tree.setViewportView(tree);

		JScrollPane aScrollPane4Table = new JScrollPane();
		aScrollPane4Table.setBorder(null);
		aScrollPane4Table.setViewportView(table);

		tabpane = new JTabbedPane();
		tabpane.addTab("文本", aScrollPane4Text);
		tabpane.addTab("可视化", aScrollPanel4ChartPanel);
		tabpane.addTab("表", aScrollPane4Table);

		splitPane.setLeftComponent(aScrollPane4Tree);
		splitPane.setRightComponent(tabpane);

	}

	private void RemoveAllRows4Table(JTable table) {
		table.invalidate();
		for (int index = table.getModel().getRowCount() - 1; index >= 0; index--) {
			((DefaultTableModel) table.getModel()).removeRow(index);
		}
		table.validate();
	}

	private void GenTableCol(DefaultTableModel model) {
		// for (int i = 0; i < 9; i++) {
		// model.addColumn("Col" + i);
		// }
		// ;

		//
		// for (int i = 0; i < 90; i++) {
		// model.addRow(new Object[] { "row" + (i + 1), "1", "2", "3", "4",
		// "5", "6", "7", "8" });
		// }
		// ;

		// init column head for table;
		this.RemoveAllRows4Table(table);
		model.addColumn("ID");
		model.addColumn("Type");
		model.addColumn("Name");
		model.addColumn("R");
		model.addColumn("W");
		model.addColumn("E");
		model.addColumn("H");
		model.addColumn("最后修改时间");
		model.addColumn("Size（KB）");
		model.addColumn("Size（MB）");
		model.addColumn("绝对路径");

		// table.setModel(model);
		// table.invalidate();
		// table.repaint();

	}

	private void GenData4Tree(DefaultMutableTreeNode node) {
		// JTREE树控件节点的图标是根据是否有children来确定是否是LEAF的。

		DefaultMutableTreeNode tempTreeNode = null;
		Node tempNode = null;

		String path = getNodeFromTreeNode(node).getPath();

		this.RemoveAllRows4Table(table);
		// 初始化将根目录显示在TREE中，并添加下一层的节点（总计2层，其他分支和叶子等待点击再处理）
		File file = new File(path);
		if (file.exists()) {
			try {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					// for (int i = files.length-1; i >=0; i--) {
					if (files[i].isFile()) {
						// System.out.println("add LEAF[" + tempTreeNode + "]");
					} else if (files[i].isDirectory()) {
						// display branch/dir node
						tempNode = new Node(files[i].getName(),
								files[i].getAbsolutePath(), true);
						tempTreeNode = new DefaultMutableTreeNode(tempNode);
						node.add(tempTreeNode);
						// System.out.println("add DIR[" + tempTreeNode + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	// private void GenData4Tree(String path, DefaultMutableTreeNode node) {
	// // JTREE树控件节点的图标是根据是否有children来确定是否是LEAF的。
	//
	// // path为空则设置为默认“C://”
	// if (path.length() == 0) {
	// path = "C://";
	// }
	//
	// DefaultMutableTreeNode tempTreeNode = null;
	// Node tempNode = null;
	//
	// this.RemoveAllRows4Table(table);
	// // 初始化将根目录显示在TREE中，并添加下一层的节点（总计2层，其他分支和叶子等待点击再处理）
	// File file = new File(path);
	// if (file.exists()) {
	// try {
	// File files[] = file.listFiles();
	// for (int i = 0; i < files.length; i++) {
	// // for (int i = files.length-1; i >=0; i--) {
	// if (files[i].isFile()) {
	// // System.out.println("add LEAF[" + tempTreeNode + "]");
	// } else if (files[i].isDirectory()) {
	// // display branch/dir node
	// tempNode = new Node(files[i].getName(),
	// files[i].getAbsolutePath(), true);
	// tempTreeNode = new DefaultMutableTreeNode(tempNode);
	// node.add(tempTreeNode);
	// // System.out.println("add DIR[" + tempTreeNode + "]");
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// } else {
	// }
	// }

	private void GenData4Table(String path) {
		// path为空则设置为默认“C://”
		if (path == null)
			return;
		table.invalidate();
		this.RemoveAllRows4Table(table);
		TheFile oneFile = null;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// 初始化将根目录显示在TREE中，并添加下一层的节点（总计2层，其他分支和叶子等待点击再处理）
		File file = new File(path);
		if (file.exists()) {
			// If having file OR dir under path, then produce them
			// Otherwise, return
			try {
				File files[] = file.listFiles();

				for (int i = 0; i < files.length; i++) {
					oneFile = TheFileHelper.GenTheFile(files[i]);
					// append row to table
					AppendOneRow(model, oneFile, i+1);
					// System.out.println("append line(LEAF/DIR) to table["
					// + oneFile.getName() + "]");
					label.setText("append line(LEAF/DIR) to table["
							+ oneFile.getName() + "]");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
		table.validate();
	}

	private void GenChart4ChartPanel(String path) {
		// 对文件集产生chart饼图。

		if (path == null)
			return;
		DefaultPieDataset piedataset = new DefaultPieDataset();
		File file = new File(path);
		if (file.exists()) {
			try {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						// 添加文件数据
						piedataset.setValue(files[i].getName(),
								files[i].length() / 1024l);
					} else if (files[i].isDirectory()) {
						// 不添加目录数据
					}
				}
				// 画饼图
				if (piedataset != null) {
					chartPanel.removeAll();
					chartPanel.setChart(MakePieChart4FileSize(piedataset, path,
							path + ".png", null));
					// chartPanel.updateUI();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon CreateImageIcon(String path) {
		java.net.URL imgURL = DiskView3.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		if (mode.isDISK_VIEW()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();

			if (node == null)
				return;

			Node oneNode = getNodeFromTreeNode(node);
			String path = oneNode.getPath();
			if (oneNode.IsDir()) {
				// 如果是目录，按需要更新table，tree和chart
				if (node.getChildCount() <= 0) {
					// 如果目录下面为空，则可能没有被浏览/点击过，所以就需要解析和添加子节点。
					// 当然，如果实际上是一个空目录，虽然不需要下面操作，但是为coding方便和一致性，所以执行下面命令
					// GenData4Tree(path, node);
					GenData4Tree(node);
				}
				GenChart4ChartPanel(path);// 更新chart
				GenData4Table(path);// 更新table

			} else {
				// 虽然是逻辑上的目录，但形式上是控件TREE最下级的LEAF。
				// 如果用Node逻辑判断，都是DIR不可能进入这个分支！！！
				if (DEBUG) {
					System.out.println("LEAF [" + oneNode.getPath()
							+ "]: is clicked！这行不可能出现！左树都是逻辑文件夹不可能出现文件！");
				}
				// 展开，并将下面文件和目录信息显示在！
			}
		} else if (mode.isDISK_SCAN()) {
			// 此模式下动作，不因为TREE控件上的操作而影响TABLE等其他控件。

		} else {
			// 未知状态，ignore
		}

//		System.out.println("VALUE_CHANGED! ");
	}

	private Node getNodeFromTreeNode(DefaultMutableTreeNode node) {
		return (Node) node.getUserObject();
	}

	private void AppendOneRow(DefaultTableModel model, TheFile oneFile,
			long rowID) {
		Vector<String> rowData = new Vector<String>(2);
		rowData.add(rowID + "");
		rowData.add(oneFile.isDir() ? "目录" : "文件");
		rowData.add(oneFile.getName());
		rowData.add(oneFile.isCanRead() ? "Y" : "N");
		rowData.add(oneFile.isCanWrite() ? "Y" : "N");
		rowData.add(oneFile.isCanExecute() ? "Y" : "N");
		rowData.add(oneFile.isHidden() ? "Y" : "N");

		Date date = new Date(oneFile.getLastModifitedTime());
		// SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
		String dd = sdf.format(date);
		rowData.add(dd);

		rowData.add((oneFile.getSizeKB()) + "");
		rowData.add((oneFile.getSizeMB()) + "");
		rowData.add(oneFile.getAbsolutePath());
		model.addRow(rowData);

		// table.setModel(model);
		// table.invalidate();
		// table.repaint();
	}

	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		// 打开TREE节点，等价双击打开该节点处理，如果原来没选中这个节点则会及联出发valueChanged事件！
		TreePath path = event.getPath();
		tree.setSelectionPath(path);
//		TreeSelectionEvent tse = new TreeSelectionEvent(tree, path, true, path,
//				path);
//		this.valueChanged(tse);
		// System.out.println("展开节点! path=["+path+"]");
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		// 折叠TREE节点，等价双击关闭该节点处理，如果原来没选中这个节点则会及联出发valueChanged事件！
		TreePath path = event.getPath();
		tree.setSelectionPath(path);
//		TreeSelectionEvent tse = new TreeSelectionEvent(tree, path, true, path,
//				path);
//		this.valueChanged(tse);
		// System.out.println("折叠节点! path=["+path+"]");

	}

	/**
	 * 饼状图 数据集
	 */
	public PieDataset PreparePieDataSet(String[] keys, double[] data) {
		if (keys != null && data != null) {
			if (keys.length == data.length) {
				DefaultPieDataset dataset = new DefaultPieDataset();
				for (int i = 0; i < keys.length; i++) {
					dataset.setValue(keys[i], data[i]);
				}
				return dataset;
			}
		}
		return null;
	}

	/**
	 * 饼状图
	 * 
	 * @param dataset
	 *            数据集
	 * @param chartTitle
	 *            图标题
	 * @param chartName
	 *            生成图的名字
	 * @param pieKeys
	 *            分饼的名字集
	 * @return
	 */
	public JFreeChart MakePieChart4FileSize(PieDataset dataset,
			String chartTitle, String chartFileName, String[] pieKeys) {
		// 利用工厂类来创建3D饼图
		JFreeChart chart = ChartFactory.createPieChart3D(chartTitle, dataset,
				true, true, false);

		// 使下说明标签字体清晰,去锯齿类似于
		// chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);的效果
		chart.setTextAntiAlias(false);
		// 图片背景色
		chart.setBackgroundPaint(Color.white);
		// 设置图标题的字体重新设置title(否组有些版本Title会出现乱码)
		chart.getTitle().setFont((new Font("隶书", Font.CENTER_BASELINE, 15)));
		// 设置图例(Legend)上的文字(//底部的字体)
		chart.getLegend().setItemFont(new Font("隶书", Font.CENTER_BASELINE, 12));
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		// 图片中显示百分比:默认方式
		// 指定饼图轮廓线的颜色
		plot.setBaseSectionOutlinePaint(Color.BLACK);
		plot.setBaseSectionPaint(Color.BLACK);
		// 设置无数据时的信息
		plot.setNoDataMessage("无对应的数据，请重新查询。");
		// 设置无数据时的信息显示颜色
		plot.setNoDataMessagePaint(Color.red);
		// 图片中显示百分比:自定义方式，{0} 表示选项， {1} 表示数值， {2} 表示所占比例 ,小数点后两位
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}={1}({2})", NumberFormat.getNumberInstance(),
				new DecimalFormat("0.00%")));
		// 图例显示百分比:自定义方式， {0} 表示选项， {1} 表示数值， {2} 表示所占比例
		plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}({2})"));
		plot.setLabelFont(new Font("隶书", Font.TRUETYPE_FONT, 10));
		// 指定图片的透明度(0.0-1.0)
		plot.setForegroundAlpha(0.65f);
		// 指定显示的饼图上圆形(false)还椭圆形(true)
		plot.setCircular(false, true);
		// 设置第一个 饼块section 的开始位置，默认是12点钟方向
		plot.setStartAngle(90);
		// 设置分饼颜色(不设置它会自己设置)
		// plot.setSectionPaint(pieKeys[0], new Color(244, 194, 144));
		// plot.setSectionPaint(pieKeys[1], new Color(144, 233, 144));

		// 把饼图生成图片
		// this.SaveChart(chartFileName, Tool.getUserDir(), chart);
		return chart;
	}

	private void SaveChart(String chartFileName, String dir, JFreeChart chart) {
		FileOutputStream fos_jpg = null;
		try {
			// 文件夹不存在则创建
			Tool.MakePathIfNotExist(dir);
			String chartFilename = Tool.getUserDir() + chartFileName;
			fos_jpg = new FileOutputStream(chartFilename);
			// 高宽的设置影响椭圆饼图的形状
			ChartUtilities.writeChartAsPNG(fos_jpg, chart, 700, 400);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos_jpg.close();
				System.out.println("create pie-chart.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void GenData4TreeRecursion(DefaultMutableTreeNode node, int maxDept) {
		// JTREE树控件节点的图标是根据是否有children来确定是否是LEAF的。

		DefaultMutableTreeNode tempTreeNode = null;
		Node tempNode = null;
		String path = getNodeFromTreeNode(node).getPath();
		TheFile oneFile = null;
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		// 初始化将根目录显示在TREE中，并添加下一层的节点（总计2层，其他分支和叶子等待点击再处理）
		File file = new File(path);
		if (file.exists()) {
			try {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						// APPEND file to RIGHT TABLE!
						oneFile = TheFileHelper.GenTheFile(files[i]);
						AppendOneRow(model, oneFile, Sequence.next());
						// DO NOT DISPLAY LEAF in TREE! Stop and 回溯！
						// System.out.println("add LEAF[" + tempTreeNode + "]");
						label.setText("Reach LEAF[" + tempTreeNode
								+ "],Stop and go back!");
					} else if (files[i].isDirectory()) {
						// display branch/dir node
						tempNode = new Node(files[i].getName(),
								files[i].getAbsolutePath(), true);
						tempTreeNode = new DefaultMutableTreeNode(tempNode);
						node.add(tempTreeNode);
						// System.out.println("add DIR[" + tempTreeNode + "]");
						if (maxDept >=0) {
							label.setText("Reach DIR[" + tempTreeNode
									+ "], go further!");
							GenData4TreeRecursion(tempTreeNode, maxDept--);
						} else {
							// maxDept达到，不用再深入这层。
							label.setText("Reach DIR[" + tempTreeNode
									+ "], Reach MAX DEPT，Stop and go back!");
							return;
						}
					}
					label.setVisible(false);//有用吗？
					label.setVisible(true);//有用吗？
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	// private void GenTree(String path, boolean stopThisLevel,
	// DefaultMutableTreeNode node) {
	// //JTREE树控件节点的图标是根据是否有children来确定是否是LEAF的。
	//
	// String fileSeperator = Tool.getOSFileSeparator();
	//
	// // path为空则设置为默认“C://”
	// if (path.length() == 0) {
	// path = "C://";
	// }
	//
	// DefaultMutableTreeNode tempTreeNode = null;
	// Node tempNode = null;
	//
	// File file = new File(path);
	// if (file.exists()) {
	// try {
	// File files[] = file.listFiles();
	// for (int i = 0; i < files.length; i++) {
	// // for (int i = files.length-1; i >=0; i--) {
	// if (files[i].isFile()) {
	// // display leaf/file node
	// tempNode = new Node(files[i].getName(),
	// files[i].getAbsolutePath(), false);
	// tempTreeNode = new DefaultMutableTreeNode(tempNode);
	// node.add(tempTreeNode);
	// System.out.println("add LEAF[" + tempTreeNode + "]");
	// } else if (files[i].isDirectory()) {
	// // display branch/dir node
	// tempNode = new Node(files[i].getName(),
	// files[i].getAbsolutePath(), true);
	// tempTreeNode = new DefaultMutableTreeNode(tempNode);
	// node.add(tempTreeNode);
	// System.out.println("add DIR[" + tempTreeNode + "]");
	// if (!stopThisLevel) {
	// // produce hide/next level of current branch/dir
	// // first before it be clicked!
	// // Otherwise,leave branch/dir alone, don't produce
	// // them further!
	// try {
	// DisplayPathInTree(path + fileSeperator
	// + files[i].getName(), true,
	// tempTreeNode);
	// } catch (Exception e) {
	// // 遇到比如"System Volume Information"等目录无法进入的，忽略exception。
	// // e.printStackTrace();
	// return;
	// }
	// } else {
	// // 只预读2层，如果停在这层，而这个又是DIR，添加TAG='DUMMY'的LEAF节点
	// tempTreeNode.add(new DefaultMutableTreeNode(
	// new Node("DUMMY", files[i]
	// .getAbsolutePath())));
	//
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// } else {
	// }
	// }
	//

}
