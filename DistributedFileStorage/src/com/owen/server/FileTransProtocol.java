package com.owen.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;

import com.owen.storage.SFile;
import com.owen.storage.StorageNode;
import com.owen.utils.MyUtils;

/**
 * 服务器端定义的文件传输协议
 * 是真正处理Socket请求的协议
 * @author Administrator
 *
 */
public class FileTransProtocol implements IOStrategy{
	
	@Override
	public void service(Socket socket) {
		try {
			//获取到Socket的输入输出流
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			int command = 0;
			while (true) {
				command = dis.readInt(); // 实际上，协议命令的数值
				switch (command) { 
				case 1: //上传文件，返回结点信息
					String fileName = dis.readUTF();
					long size = dis.readLong();
					System.out.println("服务器接收到上传请求，文件名为"+fileName+",大小为"+size);
					String uuid = UUID.randomUUID().toString();
					List<StorageNode> list = FileServer.getNodesForFile();
					
					//没有可用节点或可用节点个数不足两个
					if(list == null){
						dos.writeBoolean(false);
						dos.writeUTF("节点数量不足");
						dos.flush();
					}else{
						StorageNode node1 = list.get(0);
						System.out.println("发送给======="+node1.getNodeName());
						StorageNode node2 = list.get(1);
						System.out.println("发送给======="+node2.getNodeName());
						SFile sf = new SFile(fileName,size,uuid,node1,node2);
						//将文件放到列表中
						FileServer.fileList.add(sf);
						dos.writeBoolean(true);
						//向客户端返回主存储结点信息
						dos.writeUTF(node1.getNodeIP());
						dos.writeInt(node1.getNodePort());
						dos.writeUTF(node1.getRootFolder());
						//向客户端返回备份结点信息
						dos.writeUTF(node2.getNodeIP());
						dos.writeInt(node2.getNodePort());
						dos.writeUTF(node2.getRootFolder());
						//返回该文件的uuid
						dos.writeUTF(uuid);
						dos.flush();
					}
					break;
				case 2: // 下载文件
					//读取文件的uuid
					String file_id = dis.readUTF();
					SFile sfile = MyUtils.getFileFromServer(file_id);
					//在服务器中有文件
					if(sfile != null){
						dos.writeBoolean(true);
						StorageNode n1 = sfile.getStorageNode1();
						StorageNode n2 = sfile.getStorageNode2();
						//返回文件名称
						dos.writeUTF(sfile.getFileName());
						//向客户端返回主存储结点信息
						dos.writeUTF(n1.getNodeIP());
						dos.writeInt(n1.getNodePort());
						dos.writeUTF(n1.getRootFolder());
						//向客户端返回备份结点信息
						dos.writeUTF(n2.getNodeIP());
						dos.writeInt(n2.getNodePort());
						dos.writeUTF(n2.getRootFolder());
						dos.flush();
					}
					else{
						dos.writeBoolean(false);
						dos.writeUTF("该uuid对应的文件不存在");
					}
					break;
				case 3://删除文件
					//获取文件的uuid
					String f_id = dis.readUTF();
					SFile s = MyUtils.getFileFromServer(f_id);
					//在服务器中有文件
					if(s != null){
						dos.writeBoolean(true);
						StorageNode n1 = s.getStorageNode1();
						StorageNode n2 = s.getStorageNode2();
						//向客户端返回主存储结点的IP地址和端口号，用于客户端连接
						dos.writeUTF(n1.getNodeIP());
						dos.writeInt(n1.getNodePort());
						//向客户端返回两个结点的根目录
						dos.writeUTF(n1.getRootFolder());
						dos.writeUTF(n2.getRootFolder());
						dos.flush();
						//将文件从列表中删除
						FileServer.fileList.remove(s);
					}
					else{
						dos.writeBoolean(false);
						dos.writeUTF("该uuid对应的文件不存在");
					}
					break;
				case 4://上传失败,根据客户端临时保存的uuid删除fileList中的记录
					String tempId = dis.readUTF();
					SFile s_f = MyUtils.getFileFromServer(tempId);
					if(s_f != null){
						//将文件从列表中删除
						FileServer.fileList.remove(s_f);
						System.out.println("从文件列表中删除虚假记录");
					}
				}
			}
		} catch (SocketException e) {
			System.out.println("客户端断开了与服务器之间的连接");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
