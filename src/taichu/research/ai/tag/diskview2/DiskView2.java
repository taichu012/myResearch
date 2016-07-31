package taichu.research.ai.tag.diskview2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

public class DiskView2 implements TreeSelectionListener, TreeWillExpandListener {

	private JFrame frame;
	private JTable table;
	private JTree tree;
	private JTabbedPane tabpane;
	private JTextPane txtpane;
	// private JFreeChart chart;
	private ChartPanel chartPanel;
	private static boolean DEBUG = true;
	private static final String rootPath = "C:\\";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiskView2 window = new DiskView2();
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
	public DiskView2() {
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

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootPath);
		Node rootNode = new Node(rootPath, rootPath, true);
		root.setUserObject(rootNode);
		// createNodes(root);
		tree = new JTree(root);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// 节点被展开，但是选中没有改变。.
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

		// 用默认的图标（文件夹图标）设置为leaf图标
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(renderer.getDefaultClosedIcon());
		tree.setCellRenderer(renderer);

		DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		InitTable(model);
		table.setBackground(Color.GRAY);
		// table.updateUI();
		// table.invalidate();
		// table.repaint();

		JScrollPane aScrollPaneLeft = new JScrollPane();
		aScrollPaneLeft.setBorder(null);
		aScrollPaneLeft.setViewportView(tree);

		JScrollPane aScrollPane4Table = new JScrollPane();
		aScrollPane4Table.setBorder(null);
		aScrollPane4Table.setViewportView(table);

		txtpane = new JTextPane();

		JScrollPane aScrollPane4Text = new JScrollPane();
		aScrollPane4Text.setBorder(null);
		aScrollPane4Text.setViewportView(txtpane);

		// 初始化chart数据
		chartPanel = new ChartPanel(
				this.makePieChart4FileSize(new DefaultPieDataset()));

		JScrollPane aScrollPanel4ChartPanel = new JScrollPane();
		aScrollPanel4ChartPanel.setBorder(null);
		aScrollPanel4ChartPanel.setViewportView(chartPanel);

		tabpane = new JTabbedPane();
		tabpane.addTab("可视化", aScrollPanel4ChartPanel);
		tabpane.addTab("表", aScrollPane4Table);
		tabpane.addTab("文本", aScrollPane4Text);

		splitPane.setLeftComponent(aScrollPaneLeft);
		splitPane.setRightComponent(tabpane);

		// 用TREE显示DIR，用TABLE显示file。
		DisplayPathInTree("C://", root);

	}

	private void RemoveAllRow(JTable table) {
		for (int index = table.getModel().getRowCount() - 1; index >= 0; index--) {
			((DefaultTableModel) table.getModel()).removeRow(index);
		}
	}

	private void InitTable(DefaultTableModel model) {
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
		this.RemoveAllRow(table);
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

	private void DisplayPathInTree(String path, DefaultMutableTreeNode node) {
		// JTREE树控件节点的图标是根据是否有children来确定是否是LEAF的。

		// path为空则设置为默认“C://”
		if (path.length() == 0) {
			path = "C://";
		}

		DefaultMutableTreeNode tempTreeNode = null;
		Node tempNode = null;
		// table.removeAll();// clear table first.
		// table.invalidate();
		this.RemoveAllRow(table);
//		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// 初始化将根目录显示在TREE中，并添加下一层的节点（总计2层，其他分支和叶子等待点击再处理）
		File file = new File(path);
		if (file.exists()) {
			// If having file OR dir under path, then produce them
			// Otherwise, return
			try {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					// for (int i = files.length-1; i >=0; i--) {
					if (files[i].isFile()) {
//						// append row to table
//						appendRow(model, genObjTheFile(files[i]), i);
//						System.out.println("add LEAF[" + tempTreeNode + "]");
					} else if (files[i].isDirectory()) {
						// display branch/dir node
						tempNode = new Node(files[i].getName(),
								files[i].getAbsolutePath(), true);
						tempTreeNode = new DefaultMutableTreeNode(tempNode);
						node.add(tempTreeNode);
						// append row to table
//						appendRow(model, genObjTheFile(files[i]), i);
						System.out.println("add DIR[" + tempTreeNode + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}
	
	private void GenData4Table(String path) {
		// path为空则设置为默认“C://”
		if (path == null)
			return;
		this.RemoveAllRow(table);
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
					oneFile = genObjTheFile(files[i]);
						// append row to table
						appendRow(model, oneFile, i);
						System.out.println("append line(LEAF/DIR) to table[" + oneFile.getName() + "]");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
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
					chartPanel.setChart(makePieChart4FileSize(piedataset));
					chartPanel.updateUI();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	// 根据OS的文件files获得逻辑对象TheFile
	TheFile genObjTheFile(File f) {

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

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = DiskView2.class.getResource(path);
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
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		Node oneNode = (Node) nodeInfo;
		if (oneNode.IsDir()) {
			// 如果是目录，就展开，并将下面文件和目录信息显示在！
			// DisplayContain4Dir(oneNode.getPath(), table);
			if (node.getChildCount() <= 0) {
				DisplayPathInTree(oneNode.getPath(), node);
			}
			GenChart4ChartPanel(oneNode.getPath());
			GenData4Table(oneNode.getPath());

		} else {
			// 虽然是逻辑上的目录，但形式上是最下级的LEAF。
			if (DEBUG) {
				System.out.println("LEAF [" + oneNode.getPath()
						+ "]: is clicked！这行不可能出现！左树都是逻辑文件夹不可能出现文件！");
			}
			// 展开，并将下面文件和目录信息显示在！
		}
	}


	private void appendRow(DefaultTableModel model, TheFile oneFile, int rowID) {
		// model.addColumn("Col"+i); 列头在其他地方弄！

		Vector<String> rowData = new Vector<String>(2);
		rowData.add(rowID + 1 + "");
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
		table.repaint();
	}

	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		// 打开TREE节点，当作点击了该节点处理！
		TreePath path = event.getPath();
		tree.setSelectionPath(path);
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		// 折叠tree节点不处理

	}

	// public JFreeChart makePieChart() {
	// String[] keys = { "淘宝网", "拍拍网", "凡客诚品", "当当网", "eBay", "其它" };
	// double[] data = { 40.2, 14.1, 13.5, 15.8, 8.2, 9.8 };
	// return createValidityComparePimChar(getPieDataSet(keys, data),
	// "2010中国电子商务网站市场份额统计报告", "pieChart.png", keys);
	// }

	public JFreeChart makePieChart4FileSize(PieDataset piedataset) {
		return createValidityComparePimChar(piedataset, "当前目录文件大小分布饼图",
				"pieChart4FileSize.png", null);
	}

	/**
	 * 饼状图 数据集
	 */
	public PieDataset getPieDataSet(String[] keys, double[] data) {
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
	 * @param charName
	 *            生成图的名字
	 * @param pieKeys
	 *            分饼的名字集
	 * @return
	 */
	public JFreeChart createValidityComparePimChar(PieDataset dataset,
			String chartTitle, String charName, String[] pieKeys) {
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
		FileOutputStream fos_jpg = null;
		try {
			// 文件夹不存在则创建
			// isChartPathExist("C:\\temp\\");
			// String chartName = "C:\\temp\\" + charName;
			isChartPathExist(Tool.getUserDir());
			String chartName = Tool.getUserDir() + charName;
			fos_jpg = new FileOutputStream(chartName);
			// 高宽的设置影响椭圆饼图的形状
			ChartUtilities.writeChartAsPNG(fos_jpg, chart, 700, 400);

			return chart;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				fos_jpg.close();
				System.out.println("create pie-chart.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断文件夹是否存在，如果不存在则新建
	 * 
	 * @param chartPath
	 */
	private void isChartPathExist(String chartPath) {
		File file = new File(chartPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

}
