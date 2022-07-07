package com.owen.storage;

import java.io.Serializable;

/**
 * 存储结点
 * @author Administrator
 * @param <T>
 *
 */
public class StorageNode implements Comparable,Serializable{
	private String nodeName;//结点的名字
	private String nodeIP;//结点的IP地址
	private int nodePort;//结点的端口号
	private String rootFolder;//根文件夹
	private long volume;//容量
	private long last;//剩余容量
	private String fileServerIP;//文件服务器的IP地址
	private int fileServerPort;//文件服务器的端口号
	private boolean available;//该节点可以用
	/**
	 * 构造函数
	 */
	public StorageNode(){}
	
	public StorageNode(String nodeName, String nodeIP, int nodePort, String rootFolder, long volume,long last) {
		super();
		this.nodeName = nodeName;
		this.nodeIP = nodeIP;
		this.nodePort = nodePort;
		this.rootFolder = rootFolder;
		this.volume = volume;
		this.last = last;
	}

	/**
	 * getter和setter方法
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeIP() {
		return nodeIP;
	}
	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}
	public int getNodePort() {
		return nodePort;
	}
	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}
	public String getRootFolder() {
		return rootFolder;
	}
	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public String getFileServerIP() {
		return fileServerIP;
	}
	public void setFileServerIP(String fileServerIP) {
		this.fileServerIP = fileServerIP;
	}
	public int getFileServerPort() {
		return fileServerPort;
	}
	public void setFileServerPort(int fileServerPort) {
		this.fileServerPort = fileServerPort;
	}
	public long getLast() {
		return last;
	}
	public void setLast(long last) {
		this.last = last;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * 重写toString方法
	 */
	@Override
	public String toString() {
		return nodeName+" "+nodeIP+" "+nodePort+" "+
	rootFolder+" "+volume+ " " +last;
	}

	@Override
	public int compareTo(Object o) {
		StorageNode node = (StorageNode) o;
		return (int) (node.last-last);
	}
}
