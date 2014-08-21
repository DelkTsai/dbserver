package org.enilu.socket.v3.server.threadpool.task;

/**
 * 标准的工作类，使用线程池的worker必须继承该类
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public abstract class Task {
	/**
	 * task标准的work接口，所有子类必须实现该接口，用作具体的业务处理
	 * 
	 * @return
	 */
	public abstract String work();

	/**
	 * 返回当前对象字符串标识
	 */
	public String toString() {
		return super.toString();
	};
}
