package com.owen.storage;

import java.io.Serializable;

/**
 * 传输的文件信息
 * @author Administrator
 *
 */
public class SFile implements Serializable{
	//文件名，大小，uuid，存储的结点（两个）
	private String fileName;
	private long size;
	private String uuid;
	private StorageNode storageNode1;
	private StorageNode storageNode2;
	
	public SFile(){}

	public SFile(String fileName, long size, String uuid, StorageNode storageNode1, StorageNode storageNode2) {
		super();
		this.fileName = fileName;
		this.size = size;
		this.uuid = uuid;
		this.storageNode1 = storageNode1;
		this.storageNode2 = storageNode2;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public StorageNode getStorageNode1() {
		return storageNode1;
	}

	public void setStorageNode1(StorageNode storageNode1) {
		this.storageNode1 = storageNode1;
	}

	public StorageNode getStorageNode2() {
		return storageNode2;
	}

	public void setStorageNode2(StorageNode storageNode2) {
		this.storageNode2 = storageNode2;
	}
}
