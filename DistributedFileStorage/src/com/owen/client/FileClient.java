package com.owen.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import com.owen.server.FileServer;
import com.owen.utils.MyUtils;

public class FileClient implements FileOperation{
	RemoteFileOperation fileOperation;
	private Socket socket;//客户端的套接字
	private DataInputStream dis;//Socket的输入流
	private DataOutputStream dos;//socket的输出流
	
	public FileClient() {
		//读取FileServer的配置文件，获取FileServer的IP地址和端口号
		Properties properties = MyUtils.getProperty("fileserver.properties");
		String fileServerIP = properties.getProperty("fileServerIP");
		int fileServerPort = Integer.parseInt(properties.getProperty("fileServerPort"));
		//建立与FileServer的Socket连接
		try {
			socket = new Socket(fileServerIP,fileServerPort);
			//获取Socket的输入流和输出流
			if(socket != null){
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			}			
		} catch (ConnectException e) {
			System.out.println("服务器未启动");
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 客户端上传文件，RemoteFileOperation来向该节点进行文件传输
	 */
	@Override
	public String update(String fileName) {
		//如果服务器没启动，直接上传失败
		if(socket ==null){
			return null;
		}
		File f = new File(fileName);
		String uuid = null;
		String tempId = null;
		try {
			dos.writeInt(1);
			dos.writeUTF(f.getName());
			dos.writeLong(f.length());
			dos.flush();
			if(!dis.readBoolean()){
				System.out.println(dis.readUTF());
				return null;
			}
			//获取到文件的主存储结点和备份结点的IP地址和端口号,文件夹
			String address1 = dis.readUTF();
			int port1 = dis.readInt();
			String nodefolder1 = dis.readUTF();
			System.out.println(address1+" "+port1+" "+nodefolder1);
			String address2 = dis.readUTF();
			int port2 = dis.readInt();
			String nodefolder2 = dis.readUTF();
			uuid = dis.readUTF();
			tempId = uuid;
			System.out.println(address2+" "+port2+" "+nodefolder2);
			//创建一个RemoteFileOperation对象
			fileOperation = new RemoteFileOperation(address1, port1);
			//调用fileOperation的update函数，从客户端向结点发送文件数据
			uuid = fileOperation.update(fileName+"@"+uuid+"@"+nodefolder1+"@"+nodefolder2);
			//上传失败,通知服务器删除记录
			if(uuid == null){
				dos.writeInt(4);
				dos.writeUTF(tempId);
				System.out.println("上传失败,通知服务器删除记录");
			}
			fileOperation = null;
		} catch (SocketException e) {
			System.out.println("服务器未启动或出现故障");
		}catch (IOException e) {
			e.printStackTrace();
		}
		return uuid;
	}
	/**
	 * 客户端要求下载文件，从FileServer中获取主存储结点和备份结点的信息
	 * 从主存储结点下载文件，如果主存储结点失效，则从备份结点下载
	 */
	@Override
	public boolean download(String uuid) {
		//如果服务器没启动，直接下载失败
		if(socket ==null){
			return false;
		}
		//文件的名称
		String fileName;
		//文件的结点信息
		String address1 = null;
		String address2 = null;
		int port1 = 0;
		int port2 = 0;
		String nodefolder1 = null;
		String nodefolder2 = null;
		try {
			//向FileServer发送下载的指令
			dos.writeInt(2);
			//发送文件的uuid
			dos.writeUTF(uuid);
			dos.flush();
			//文件存在
			if(dis.readBoolean()){
				//获取到文件的真实名称
				fileName = dis.readUTF();
				//获取到文件的主存储结点和备份结点的IP地址和端口号,文件夹
				address1 = dis.readUTF();
				port1 = dis.readInt();
				nodefolder1 = dis.readUTF();
				System.out.println(address1+" "+port1+" "+nodefolder1);
				address2 = dis.readUTF();
				port2 = dis.readInt();
				nodefolder2 = dis.readUTF();
				System.out.println(address2+" "+port2+" "+nodefolder2);
				boolean success = false;			
				//连接到文件的主存储结点
				fileOperation = new RemoteFileOperation(address1, port1);
				//调用fileOperation的download函数
				success = fileOperation.download(uuid+"@"+fileName+"@"+nodefolder1);
				fileOperation = null;

				if(success){
					return true;
				}
				//主节点下载失败,连接到备份结点进行下载
				else{
					//连接到文件的备份存储结点
					fileOperation = new RemoteFileOperation(address2, port2);
					//调用fileOperation的download函数
					success = fileOperation.download(uuid+"@"+fileName+"@"+nodefolder2);
					fileOperation = null;
					return success;
				}
			}else{
				System.out.println(dis.readUTF());
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 客户端请求删除数据
	 */
	@Override
	public boolean delete(String uuid) {
		//如果服务器未启动，直接删除失败
		if(socket ==null){
			return false;
		}
		// 文件的主存储结点信息，如果主存储节点删除失败了就直接失败
		String address1 = null;
		int port1 = 0;
		String nodefolder1 = null;
		String nodefolder2 = null;
		try {
			// 向FileServer发送删除的指令
			dos.writeInt(3);
			// 发送文件的uuid
			dos.writeUTF(uuid);
			dos.flush();
			// 文件存在
			if (dis.readBoolean()) {
				// 获取到文件的主存储结点和备份结点的IP地址和端口号,文件夹
				address1 = dis.readUTF();
				port1 = dis.readInt();
				nodefolder1 = dis.readUTF();
				nodefolder2 = dis.readUTF();
				System.out.println(address1 + " " + port1 + " " + nodefolder1+" "+nodefolder2);
				boolean success = false;
				// 连接到文件的主存储结点
				fileOperation = new RemoteFileOperation(address1, port1);
				// 调用fileOperation的download函数
				success = fileOperation.delete(uuid + "@" + nodefolder1+"@"+nodefolder2);
				fileOperation = null;
				return success;
			} else {
				//打印错误原因
				System.out.println(dis.readUTF());
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
