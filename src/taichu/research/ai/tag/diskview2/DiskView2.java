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

		// ��Ĭ�ϵ�ͼ�꣨�ļ���ͼ�꣩����Ϊleafͼ��
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

		// ��ʼ��chart����
		chartPanel = new ChartPanel(
				this.makePieChart4FileSize(new DefaultPieDataset()));

		JScrollPane aScrollPanel4ChartPanel = new JScrollPane();
		aScrollPanel4ChartPanel.setBorder(null);
		aScrollPanel4ChartPanel.setViewportView(chartPanel);

		tabpane = new JTabbedPane();
		tabpane.addTab("���ӻ�", aScrollPanel4ChartPanel);
		tabpane.addTab("��", aScrollPane4Table);
		tabpane.addTab("�ı�", aScrollPane4Text);

		splitPane.setLeftComponent(aScrollPaneLeft);
		splitPane.setRightComponent(tabpane);

		// ��TREE��ʾDIR����TABLE��ʾfile��
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
		model.addColumn("����޸�ʱ��");
		model.addColumn("Size��KB��");
		model.addColumn("Size��MB��");
		model.addColumn("����·��");

		// table.setModel(model);
		// table.invalidate();
		// table.repaint();

	}

	private void DisplayPathInTree(String path, DefaultMutableTreeNode node) {
		// JTREE���ؼ��ڵ��ͼ���Ǹ����Ƿ���children��ȷ���Ƿ���LEAF�ġ�

		// pathΪ��������ΪĬ�ϡ�C://��
		if (path.length() == 0) {
			path = "C://";
		}

		DefaultMutableTreeNode tempTreeNode = null;
		Node tempNode = null;
		// table.removeAll();// clear table first.
		// table.invalidate();
		this.RemoveAllRow(table);
//		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// ��ʼ������Ŀ¼��ʾ��TREE�У��������һ��Ľڵ㣨�ܼ�2�㣬������֧��Ҷ�ӵȴ�����ٴ���
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
		// pathΪ��������ΪĬ�ϡ�C://��
		if (path == null)
			return;
		this.RemoveAllRow(table);
		TheFile oneFile = null;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// ��ʼ������Ŀ¼��ʾ��TREE�У��������һ��Ľڵ㣨�ܼ�2�㣬������֧��Ҷ�ӵȴ�����ٴ���
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
		// ���ļ�������chart��ͼ��

		if (path == null)
			return;
		DefaultPieDataset piedataset = new DefaultPieDataset();
		File file = new File(path);
		if (file.exists()) {
			try {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						// ����ļ�����
						piedataset.setValue(files[i].getName(),
								files[i].length() / 1024l);
					} else if (files[i].isDirectory()) {
						// �����Ŀ¼����
					}
				}
				// ����ͼ
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

	// ����OS���ļ�files����߼�����TheFile
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
			// �����Ŀ¼����չ�������������ļ���Ŀ¼��Ϣ��ʾ�ڣ�
			// DisplayContain4Dir(oneNode.getPath(), table);
			if (node.getChildCount() <= 0) {
				DisplayPathInTree(oneNode.getPath(), node);
			}
			GenChart4ChartPanel(oneNode.getPath());
			GenData4Table(oneNode.getPath());

		} else {
			// ��Ȼ���߼��ϵ�Ŀ¼������ʽ�������¼���LEAF��
			if (DEBUG) {
				System.out.println("LEAF [" + oneNode.getPath()
						+ "]: is clicked�����в����ܳ��֣����������߼��ļ��в����ܳ����ļ���");
			}
			// չ�������������ļ���Ŀ¼��Ϣ��ʾ�ڣ�
		}
	}


	private void appendRow(DefaultTableModel model, TheFile oneFile, int rowID) {
		// model.addColumn("Col"+i); ��ͷ�������ط�Ū��

		Vector<String> rowData = new Vector<String>(2);
		rowData.add(rowID + 1 + "");
		rowData.add(oneFile.isDir() ? "Ŀ¼" : "�ļ�");
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
		// ��TREE�ڵ㣬��������˸ýڵ㴦��
		TreePath path = event.getPath();
		tree.setSelectionPath(path);
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		// �۵�tree�ڵ㲻����

	}

	// public JFreeChart makePieChart() {
	// String[] keys = { "�Ա���", "������", "���ͳ�Ʒ", "������", "eBay", "����" };
	// double[] data = { 40.2, 14.1, 13.5, 15.8, 8.2, 9.8 };
	// return createValidityComparePimChar(getPieDataSet(keys, data),
	// "2010�й�����������վ�г��ݶ�ͳ�Ʊ���", "pieChart.png", keys);
	// }

	public JFreeChart makePieChart4FileSize(PieDataset piedataset) {
		return createValidityComparePimChar(piedataset, "��ǰĿ¼�ļ���С�ֲ���ͼ",
				"pieChart4FileSize.png", null);
	}

	/**
	 * ��״ͼ ���ݼ�
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
	 * ��״ͼ
	 * 
	 * @param dataset
	 *            ���ݼ�
	 * @param chartTitle
	 *            ͼ����
	 * @param charName
	 *            ����ͼ������
	 * @param pieKeys
	 *            �ֱ������ּ�
	 * @return
	 */
	public JFreeChart createValidityComparePimChar(PieDataset dataset,
			String chartTitle, String charName, String[] pieKeys) {
		// ���ù�����������3D��ͼ
		JFreeChart chart = ChartFactory.createPieChart3D(chartTitle, dataset,
				true, true, false);

		// ʹ��˵����ǩ��������,ȥ���������
		// chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);��Ч��
		chart.setTextAntiAlias(false);
		// ͼƬ����ɫ
		chart.setBackgroundPaint(Color.white);
		// ����ͼ�����������������title(������Щ�汾Title���������)
		chart.getTitle().setFont((new Font("����", Font.CENTER_BASELINE, 15)));
		// ����ͼ��(Legend)�ϵ�����(//�ײ�������)
		chart.getLegend().setItemFont(new Font("����", Font.CENTER_BASELINE, 12));
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		// ͼƬ����ʾ�ٷֱ�:Ĭ�Ϸ�ʽ
		// ָ����ͼ�����ߵ���ɫ
		plot.setBaseSectionOutlinePaint(Color.BLACK);
		plot.setBaseSectionPaint(Color.BLACK);
		// ����������ʱ����Ϣ
		plot.setNoDataMessage("�޶�Ӧ�����ݣ������²�ѯ��");
		// ����������ʱ����Ϣ��ʾ��ɫ
		plot.setNoDataMessagePaint(Color.red);
		// ͼƬ����ʾ�ٷֱ�:�Զ��巽ʽ��{0} ��ʾѡ� {1} ��ʾ��ֵ�� {2} ��ʾ��ռ���� ,С�������λ
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}={1}({2})", NumberFormat.getNumberInstance(),
				new DecimalFormat("0.00%")));
		// ͼ����ʾ�ٷֱ�:�Զ��巽ʽ�� {0} ��ʾѡ� {1} ��ʾ��ֵ�� {2} ��ʾ��ռ����
		plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}({2})"));
		plot.setLabelFont(new Font("����", Font.TRUETYPE_FONT, 10));
		// ָ��ͼƬ��͸����(0.0-1.0)
		plot.setForegroundAlpha(0.65f);
		// ָ����ʾ�ı�ͼ��Բ��(false)����Բ��(true)
		plot.setCircular(false, true);
		// ���õ�һ�� ����section �Ŀ�ʼλ�ã�Ĭ����12���ӷ���
		plot.setStartAngle(90);
		// ���÷ֱ���ɫ(�����������Լ�����)
		// plot.setSectionPaint(pieKeys[0], new Color(244, 194, 144));
		// plot.setSectionPaint(pieKeys[1], new Color(144, 233, 144));

		// �ѱ�ͼ����ͼƬ
		FileOutputStream fos_jpg = null;
		try {
			// �ļ��в������򴴽�
			// isChartPathExist("C:\\temp\\");
			// String chartName = "C:\\temp\\" + charName;
			isChartPathExist(Tool.getUserDir());
			String chartName = Tool.getUserDir() + charName;
			fos_jpg = new FileOutputStream(chartName);
			// �߿������Ӱ����Բ��ͼ����״
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
	 * �ж��ļ����Ƿ���ڣ�������������½�
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
