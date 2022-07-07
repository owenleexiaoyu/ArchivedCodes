package com.owen.server;

import java.net.Socket;
/**
 * 用于处理Socket数据请求的接口
 * @author Administrator
 *
 */
public interface IOStrategy {
	void service(Socket socket);
}
