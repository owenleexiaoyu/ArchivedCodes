package com.owen.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.owen.server.FileServer;
import com.owen.storage.SFile;
import com.owen.storage.StorageNode;

public class MyUtils {
	public static Properties getProperty(String propertiesPath){
		Properties fileServerProperties = new Properties();
		try {
			InputStream is = new FileInputStream(propertiesPath);
			fileServerProperties.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileServerProperties;
	}
	public static long getVolume(String maxVolumn){
		String num = "";
		long volumn = 0;
		if(maxVolumn!=null){
			for(int i = 0; i < maxVolumn.length(); i++){
				if(Character.isDigit(maxVolumn.charAt(i))){
					num += maxVolumn.charAt(i);
					volumn = Long.parseLong(num);
					continue;
				}
				
				switch(maxVolumn.charAt(i)){
					case 'B':
						break;
					case 'K':
						volumn = volumn * 1024;
						break;
					case 'M':
						volumn = volumn * 1024 * 1024;
						break;
					case 'G':
						volumn = volumn * 1024 * 1024 * 1024;
						break;
					default:
					
						break;
				}
			}
		}
		return volumn;
	}
	/**
	 * 修改配置文件中各个结点的剩余容量
	 * 当上传一个文件后，就要减去这个文件的大小
	 * 当删除一个文件后，就要加上这个文件的大小，size的值为负
	 * @param propertiesPath
	 * @param size
	 * @return
	 */
	public static String setLastVolume(String propertiesPath, long size){
		String lastStr = null;
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(propertiesPath));
			long last = getVolume(p.getProperty("Last"));
			//减去文件的大小
			last -= size;
			lastStr = last+"B";
			p.setProperty("Last", lastStr);
			p.store(new FileOutputStream(propertiesPath), "update Last value");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lastStr;
	}
	/**
	 * 通过uuid查找服务器中是否有该文件,有的话返回该文件的信息，没有返回null
	 * @param uuid
	 * @return
	 */
	public static SFile getFileFromServer(String uuid){
		if(FileServer.fileList == null){
			return null;
		}
		for(SFile sf : FileServer.fileList){
			if(sf.getUuid().equals(uuid)){
				return sf;
			}
		}
		return null;
	}
	/**
	 * 获得FileServer中和参数端口相同的结点
	 * @param port
	 * @return
	 */
	public static StorageNode getNodeFromList(int port){
		if(FileServer.nodeList == null || FileServer.nodeList.size() == 0){
			return null;
		}
		for(StorageNode node : FileServer.nodeList){
			if(node.getNodePort() == port){
				return node;
			}
		}		
		return null;
	}
}
