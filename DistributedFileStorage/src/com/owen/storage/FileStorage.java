package com.owen.storage;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.owen.server.IOStrategy;
import com.owen.utils.MyUtils;
import com.owen.utils.Tool;

public class FileStorage {
	private StorageNode mNode;
	private Timer mTimer;
	private String mFileServerIP;
	private int mFileServerPort;
	private ServerSocket ss;
	public FileStorage(Properties p){
		String nodeName = p.getProperty("NodeName");
		String nodeIP = p.getProperty("NodeIP");
		int nodePort = Integer.parseInt(p.getProperty("NodePort"));
		String rootFolder = p.getProperty("RootFolder");
		System.out.println(p.getProperty("Volume"));
		long volume = MyUtils.getVolume(p.getProperty("Volume"));
		long last = MyUtils.getVolume(p.getProperty("Last"));
		//获得FileServer的IP和端口号，保存到成员变量中，用于发送UDP连接
		mFileServerIP = p.getProperty("FileServerIP");
		mFileServerPort = Integer.parseInt(p.getProperty("FileServerPort"));
		mNode = new StorageNode(nodeName, nodeIP, nodePort, rootFolder, volume, last);
		mNode.setAvailable(true);
		System.out.println(mNode.toString());
		//创建线程来不断接收FileClient的Socket请求
		IOStrategy ios = new FileProtocol();
		try {
			ss = new ServerSocket(nodePort);
			System.out.println("["+nodeName+"] is ready");
			new Thread(new Runnable() {
				@Override
				public void run() {
					//不断接收FileClient的socket连接
					while(true){
						try {
							if(!ss.isClosed()){
								Socket s = ss.accept();
								ios.service(s);
							}
						} catch (SocketException e) {
							System.out.println("节点关闭");
						}catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					//创建后不断向FileServer发送Udp数据包
					sendUDP(mFileServerIP, mFileServerPort, mNode);
				}
			}, 2000,2000);
		} catch (BindException e) {
			System.out.println("端口被占用,已自动关闭GUI");
			System.exit(0);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 向FileServer发送UDP的数据包
	 * @param ip
	 * @param port
	 * @param o
	 */
	public void sendUDP(String ip, int port, StorageNode node){
		try{
			//将Object对象序列化
			
			byte [] data = Tool.serialize(node);
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(data, data.length);

			packet.setSocketAddress(new InetSocketAddress(ip, port));
			socket.send(packet);
		}
		catch (Exception e) {
		}
	}
	public void closeNode(){
		//将结点设置为不可用
		mNode.setAvailable(false);
		//将不可用的信息发送给FileServer
		sendUDP(mFileServerIP, mFileServerPort, mNode);
		try {
			ss.close();
		} catch (NoSuchElementException e) {
			System.out.println("节点关闭");
		} catch (ArrayIndexOutOfBoundsException e) {
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			mNode = null;
			if(mTimer != null){
				mTimer.cancel();
				mTimer = null;
			}
		}
	}
//	public static void main(String[] args) {
//		//初始化存储结点
//		new FileStorage(MyUtils.getProperty("nodes\\node1.properties"));	
//		new FileStorage(MyUtils.getProperty("nodes\\node2.properties"));
//		new FileStorage(MyUtils.getProperty("nodes\\node3.properties"));
//		new FileStorage(MyUtils.getProperty("nodes\\node4.properties"));
//	}
}
