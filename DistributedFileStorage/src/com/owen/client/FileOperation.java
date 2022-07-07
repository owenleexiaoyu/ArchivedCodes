package com.owen.client;
/**
 * 定义对文件操作的接口
 * @author Administrator
 *
 */
public interface FileOperation {
	//上传文件，返回一个uuid
	String update(String fileName);
	//下载文件,利用uuid来查找
	boolean download(String uuid);
	//删除文件
	boolean delete(String uuid);
}
