package com.owen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.FileChooserUI;
import javax.swing.table.TableModel;

import com.owen.client.FileOperation;
import com.owen.server.FileServer;
import com.owen.server.FileTransProtocol;
import com.owen.server.ThreadPoolSupport;
import com.owen.storage.SFile;
import com.owen.storage.StorageNode;
import com.owen.utils.MyUtils;

public class FileServerGUI extends JFrame{
	JTabbedPane jTabbedPane = null; // 选项卡组件
	JScrollPane fileScrollPane = null;
	JScrollPane nodeScrollPane = null;
	private JTable fileTable;//显示文件的表格
	private JTable nodeTable;//显示结点的表格
	JButton refresh =null;  //刷新
	private Timer timer;//Timer用于定时刷新数据
    public static void main(String[] args) {
    	new FileServerGUI(FileServer.fileList, FileServer.nodeList);
    	new FileServer(4000, new ThreadPoolSupport(new FileTransProtocol()));
    }  
    public FileServerGUI(List<SFile> fileList, List<StorageNode> nodeList) {
    	super("FileServer");
    	fileTable = new JTable(new FileTableModel(fileList));
    	nodeTable = new JTable(new NodeTableModel(nodeList));
    	refresh=new JButton("刷新"); 
    	refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshData(FileServer.fileList, FileServer.nodeList);
				if(timer != null){
					timer.cancel();
					timer = null;
				}
				//启动一个Timer，定时刷新数据,每四秒刷新一次
				timer = new Timer();
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						refreshData(FileServer.fileList, FileServer.nodeList);
					}
				}, 4000, 4000);
			}
		});
    	fileTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // 设置字体

    	fileTable.setFillsViewportHeight(true); // 高度和滚动窗格的高度一致
		// 设置列宽
    	fileTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    	fileTable.getColumnModel().getColumn(2).setPreferredWidth(60);
    	fileTable.getColumnModel().getColumn(3).setPreferredWidth(170);
		// 设置行高
    	fileTable.setRowHeight(22);
		fileScrollPane = new JScrollPane(fileTable);
		// 设置滚动策略
		fileScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fileScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
		nodeTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // 设置字体

		nodeTable.setFillsViewportHeight(true); // 高度和滚动窗格的高度一致
		// 设置列宽
		nodeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		nodeTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		nodeTable.getColumnModel().getColumn(3).setPreferredWidth(170);
		// 设置行高
		nodeTable.setRowHeight(22);
		nodeScrollPane = new JScrollPane(nodeTable);
		// 设置滚动策略
		nodeScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		nodeScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		// 创建一个选项卡组件
		jTabbedPane = new JTabbedPane();
		// 添加选项卡
		jTabbedPane.add("上传文件", fileScrollPane);
		jTabbedPane.add("可用结点", nodeScrollPane);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(jTabbedPane); // 添加选项卡到窗口中
		getContentPane().add(refresh,BorderLayout.SOUTH);
		// 设置显示窗口居于屏幕中央位置
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((sd.width - 600) / 2, (sd.height - 400) / 2);
		setSize(600, 400);
		setVisible(true);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new MyWindowsListener());
		
	}  
    public void refreshData(List<SFile> fList, List<StorageNode> nList){
    	//重新设置表格的TableModel
    	fileTable.setModel(new FileTableModel(fList));
		nodeTable.setModel(new NodeTableModel(nList));
		
		//将所有已经上传的数据序列化后，写入文件
		File file = new File("fileconfig.txt");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			for (SFile sf : fList) {
				try {
					oos.writeObject(sf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//重新加载所有可用结点的剩余容量
		for(StorageNode node : nList){
			String nodeprop = "nodes\\"+node.getNodeName()+".properties";
			Properties p = MyUtils.getProperty(nodeprop);
			long last = MyUtils.getVolume(p.getProperty("Last"));
			node.setLast(last);
		}
    } 
    class MyWindowsListener implements WindowListener{

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("服务器关闭");
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}
    	
    }
}
