package com.owen.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadPoolSupport implements IOStrategy{
	//线程池所管理的线程列表
	private List threads = new ArrayList();
	//初始化的时候开辟多个线程
	private static final int THREAD_NUM = 50;
	//线程池中最多能容纳的线程个数
	private static final int THREAD_MAX = 100;
	//协议对象
	private IOStrategy ios = null;
	/**
	 * 构造函数，从外界传入实际的协议对象
	 * 并在初始化时生成多个线程
	 * @param ios
	 */
	public ThreadPoolSupport(IOStrategy ios) {
		this.ios = ios;
		for(int i = 0;i<THREAD_MAX;i++){
			IOThread t = new IOThread(ios);
			t.start();
			//加入线程池中
			threads.add(t);
		}
		//等待线程池中的线程都运行
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void service(Socket socket) {
		IOThread t = null;
		//是否找到空闲线程
		boolean found = false;
		//遍历线程池中所有线程，找到一个空闲的线程
		for(int i = 0;i<threads.size();i++){
			t = (IOThread) threads.get(i);
			if(t.isIdle()){
				found = true;
				break;
			}
		}
		//没有找到空闲的线程，并且当前线程的个数没超过最大限制，则创建线程
		if(!found && threads.size() < THREAD_MAX){
			t = new IOThread(ios);
			t.start();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threads.add(t);
		}
		t.setSocket(socket);
	}
}
