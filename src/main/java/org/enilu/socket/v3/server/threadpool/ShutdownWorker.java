package org.enilu.socket.v3.server.threadpool;

/**
 * 服务器停止worker
 * <p>
 * <li>该worker用来标识服务器即将被停止，当ServiceEngine读取到该worker的时候，不再监听工作队列，准备停止工作</li>
 * <p>
 * 2014年8月19日
 * <p>
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class ShutdownWorker extends Worker {

	@Override
	public String work() {
		return "shutdown";

	}

	@Override
	public String toString() {
		return "shutdown";
	}

}
