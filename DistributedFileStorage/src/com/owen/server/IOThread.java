package com.owen.server;

import java.net.Socket;

public class IOThread extends Thread{
	//真正的协议对象
	private IOStrategy ios;
	//Socket对象
	private Socket socket;
	/**
	 * 构造函数，传入实际的协议
	 * @param ios
	 */
	public IOThread(IOStrategy ios){
		this.ios = ios;
	}
	
	/**
	 * 设置Socket
	 * @param socket
	 */
	public synchronized void setSocket(Socket socket){
		this.socket = socket;
		//唤醒线程
		notify();
	}
	
	/**
	 * 判断一个县城是否是空闲的
	 * @return
	 */
	public boolean isIdle(){
		return socket == null;
	}
	/**
	 * run方法中具体执行Socket的服务
	 * 将run方法定义成synchronized的是因为调用wait（）必有对象锁
	 */
	@Override
	public synchronized void run() {
		while(true){
			try {
				//进入线程体后，立即阻塞，等待有Socket对象传进来时将它唤醒
				wait();
				//使用协议来执行Socket的服务
				ios.service(socket);
				//执行完毕后线程回到空闲状态
				socket = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
