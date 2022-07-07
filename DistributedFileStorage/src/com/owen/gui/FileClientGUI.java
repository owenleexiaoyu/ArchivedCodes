package com.owen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.owen.client.FileClient;

public class FileClientGUI extends JFrame{
	private FileClient fileClient;
	JScrollPane jScrollPane = null;
	private JButton btnUpdate;//上传文件的按钮
	private JButton btnDownload;//下载文件的按钮
	private JButton btnDelete;//删除文件的按钮
	private JLabel lbUUID;
	private JTextField tfUUID;//下载和删除的时候输入uuid
	private JTextArea taMsg;//显示一些信息
	public FileClientGUI(String title){
		super(title);
		//初始化一个FileClient对象，点击不同的按钮来调用FileClient的不同方法
		fileClient = new FileClient();
		//初始化控件
		btnUpdate = new JButton("上传文件"); 
		btnDownload = new JButton("下载文件");
		btnDelete = new JButton("删除文件");
		lbUUID = new JLabel("UUID");
		tfUUID = new JTextField();
		tfUUID.setPreferredSize(new Dimension(220, 28));
		taMsg = new JTextArea();
		taMsg.setSize(480, 350);
		jScrollPane = new JScrollPane(taMsg);
		// 设置滚动策略
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
		//设置JPanel的布局为FlowLayout
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEADING));
		//添加按钮控件到面板JPanel中
        jp.add(btnUpdate); 
        jp.add(lbUUID);
        jp.add(tfUUID);
        jp.add(btnDownload); 
        jp.add(btnDelete); 
        //将JPanel添加到顶层容器中
        getContentPane().add(jp,BorderLayout.NORTH);
        getContentPane().add(jScrollPane,BorderLayout.CENTER);
        setSize(550, 400);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - 720) / 2, (d.height - 400) / 2);
		setVisible(true); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        btnUpdate.addActionListener(new UpdateActionListener());
        btnDownload.addActionListener(new DownloadActionListener());
        btnDelete.addActionListener(new DeleteActionListener());
	}
	public static void main(String[] args) {
		new FileClientGUI("FileClient");
	}
	/**
	 * 上传文件按钮的事件处理
	 * 打开文件选择器，选择一个文件，获取到该文件，进行上传
	 * @author Administrator
	 *
	 */
	class UpdateActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc=new JFileChooser();  
	        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
	        jfc.showDialog(new JLabel(), "选择");  
	        File file=jfc.getSelectedFile();  
	        if(file == null){
	        	taMsg.append("未选择文件\n");
	        }
	        else if(file.isDirectory()){
	        	taMsg.append("暂不支持上传文件夹，请选择一个文件\n");
	        }
	        else{
	        	taMsg.append("上传文件:"+file.getName()+"\n");
		        String uuid = fileClient.update(file.getAbsolutePath());
		        if(uuid != null){
		        	taMsg.append("文件上传成功,uuid为："+uuid+"\n");
		        }else{
		        	taMsg.append("文件上传失败\n");
		        }
	        }
		}
	}
	/**
	 * 下载文件按钮的点击事件处理，输入文件的uuid来进行文件下载
	 * @author Administrator
	 *
	 */
	class DownloadActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String uuid = tfUUID.getText();
			if(uuid == null || uuid.equals("")){
				taMsg.append("请输入文件uuid\n");
			}else{
				taMsg.append("下载文件:"+uuid+"\n");
				boolean success = fileClient.download(uuid);
				taMsg.append("文件下载"+(success?"成功\n":"失败\n"));
			}
		}
	}
	/**
	 * 删除文件按钮的点击事件处理，输入文件的uuid来进行文件删除
	 * @author Administrator
	 *
	 */
	class DeleteActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String uuid = tfUUID.getText();
			if(uuid == null || uuid.equals("")){
				taMsg.append("请输入文件uuid\n");
			}else{
				taMsg.append("删除文件:"+uuid+"\n");
				boolean success = fileClient.delete(uuid);
				taMsg.append("文件删除"+(success?"成功\n":"失败\n"));
			}
		}
	}

}
