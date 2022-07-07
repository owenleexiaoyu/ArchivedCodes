package com.owen.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.Properties;

import com.owen.utils.Tool;
import com.owen.utils.ZipUtils;

/**
 * 客户端与存储结点进行数据传输的类
 * @author Administrator
 *
 */
public class RemoteFileOperation implements FileOperation{
	private String host;//结点的主机信息
	private int port;//结点的端口号
	private Socket socket;//客户端的Socket
	private DataInputStream dis;
	private DataOutputStream dos;
	BufferedInputStream bis;
	public RemoteFileOperation(String host,int port){
		try {
			socket = new Socket(host, port);
			if(socket != null){
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			System.out.println("存储节点没启动或出现故障");
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	@Override
	public String update(String fileName) {
		if(socket == null){
			return null;
		}
		String[] str = fileName.split("@");
		//获取uuid
		String uuid = str[1];
		//对原始文件进行压缩
		File zipFile = new File("E:\\"+uuid+".zip");
//		System.out.println("====>"+zipFile.getAbsolutePath());
		ZipUtils.zip(str[0], zipFile.getAbsolutePath());
		//获取结点的文件夹
		String nodeFolder1 = str[2];
		String nodeFolder2 = str[3];
		//2.对压缩后的文件进行上传
		byte [] buffer = new byte[1024 * 1024 *2];
		
		//上传到结点
		try {
			bis = new BufferedInputStream(new FileInputStream(zipFile));
			//向结点发送上传文件的指令
			dos.writeInt(1);
			//发送uuid
			dos.writeUTF(uuid);
			dos.writeUTF(nodeFolder1);
			dos.writeUTF(nodeFolder2);
			dos.flush();
			//表示可以发送文件长度及内容了
			
			if(dis.readInt() == 0){
				//循环发送指令，长度，和内容
				int len = 0;
				while((len = bis.read(buffer)) != -1){
					dos.writeInt(2);
					dos.writeInt(len);
//					System.out.println("每次发送文件的长度---》"+len);
					dos.write(buffer, 0, buffer.length);
					dos.flush();
				}
				//接收文件数据成功
				if(dis.readInt() == 0){
					//发送结束指令
					dos.writeInt(3);
					dos.flush();
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//上传成功后，删除压缩过的文件
			zipFile.delete();
			socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uuid;
	}

	@Override
	public boolean download(String uuid) {
		if(socket == null){
			return false;
		}
		boolean result = false;
		String [] str = uuid.split("@");
		//获得真正的文件uuid和文件路径
		String file_id = str[0];
		String file_name = str[1];
		String file_folder = str[2];
		File f = new File("D:\\"+file_id+".zip");
		BufferedOutputStream bos =null;
		try {
			if(!f.exists()){
				f.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//发送下载指令
			dos.writeInt(4);
			//发送uuid和文件路径
			dos.writeUTF(file_id);
			dos.writeUTF(file_folder);
			dos.flush();
			//如果有这个文件
			result = dis.readInt() == 0;
			
			if(result){
				//发送下载文件内容的指令
				dos.writeInt(5);
				//发送一个buffer的长度
				dos.writeInt(1024 * 1024 * 2);
				dos.flush();
				bos = new BufferedOutputStream(new FileOutputStream(f));
				int len = 0;
				byte[] buffer = null;
				while((len = dis.readInt()) != -1){
//					System.out.println("客户端接收文件长度===>"+len);
					buffer = new byte[len];
					dis.readFully(buffer);
					bos.write(buffer);
				}
				bos.flush();
				bos.close();
			}
			//被删除
			else{
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ZipUtils.unzip(f.getAbsolutePath(), f.getParent()+file_name);
		f.delete();
		try {
			socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean delete(String uuid) {
		if(socket == null){
			return false;
		}
		String [] str = uuid.split("@");
		
		boolean result = false;
		try {
			dos.writeInt(6);//输入删除文件的指令
			dos.writeUTF(str[0]);//输入要删除的文件的uuid
			dos.writeUTF(str[1]);//輸入主存储结点的根目录
			dos.writeUTF(str[2]);//输入备份节点的根目录
			dos.flush();
			//是否存在该文件，不存在则会失败
			result = dis.readInt() == 0;
			socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
