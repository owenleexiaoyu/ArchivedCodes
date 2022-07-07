package com.owen.server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.owen.storage.SFile;
import com.owen.storage.StorageNode;
import com.owen.utils.MyUtils;
import com.owen.utils.Tool;


public class FileServer {
	public static List<SFile> fileList = new ArrayList<>();//上传的文件列表
	public static List<StorageNode> nodeList = new ArrayList<>();//可用的存储结点列表
	private ServerSocket ss;
	public FileServer(int port, IOStrategy ios){
		//初始化两个列表
		fileList = new ArrayList<>();
		nodeList = new ArrayList<>();
		//FileServer初始化时会从文件中加载所有上传的文件
		File file = new File("fileconfig.txt");
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object o = new Object();
			while((o = ois.readObject()) != null){
				SFile f = (SFile) o;
				fileList.add(f);
			}
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DatagramSocket serverSocket = new DatagramSocket(4000);
			new Thread(){
				public void run() {
					while (true) {
						
						byte[] buffer = new byte[1024 * 16];
						DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
						try {
							serverSocket.receive(recvPacket);
						} catch (IOException e) {
							e.printStackTrace();
						}
						InetSocketAddress remoteAddress = new InetSocketAddress(recvPacket.getAddress(),
								recvPacket.getPort());
						
						byte[] data = recvPacket.getData();
						byte[] recvData = new byte[recvPacket.getLength()];
						System.arraycopy(buffer, 0, recvData, 0, recvData.length);
					
						StorageNode node = (StorageNode) Tool.deserialize(data);
						//判断是不是有效的结点信息
						if(node.getNodeName() != null && node.isAvailable()){
							StorageNode n = MyUtils.getNodeFromList(node.getNodePort());
							if(n == null){
								nodeList.add(node);
								System.out.println("节点就绪："+node.toString());
							}
							else{
								node.setLast(n.getLast());
							}
							
						}
						//如果一个节点不可用了就从列表中删除
						if(node.getNodeName() != null && !node.isAvailable()){
							nodeList.remove(MyUtils.getNodeFromList(node.getNodePort()));
						}
					}
				};
			}.start();

		} catch (BindException e) {
//			e.printStackTrace();
			System.out.println("服务器端口被占用，你已经启动了一个服务器,已自动关闭GUI");
			System.exit(0);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		//启动FileServer 的监听
		try {
			ss = new ServerSocket(port);
			//打印提示
			System.out.println("服务器准备就绪");
			while(true){
				Socket s = ss.accept();
				ios.service(s);
			}
		} catch (BindException e) {
//			e.printStackTrace();
			System.out.println("服务器端端口被占用，你已经启动了一个服务器,已自动关闭GUI");
			System.exit(0);
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * 负载均衡的函数
	 */
	public static List<StorageNode> getNodesForFile(){
		if(nodeList.size() < 2){
			return null;
		}
		else{
			Collections.sort(nodeList);
			return nodeList.subList(0, 2);
		}
	}
	
}
