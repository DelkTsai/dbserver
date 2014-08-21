package org.enilu.socket.v3.server.threadpool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.Constants;
import org.enilu.socket.v3.server.threadpool.task.Task;

/**
 * 自定义的线程类，用作线程池中维护的的标准线程对象
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class TaskThread extends Thread {
	private static Logger logger = Logger.getLogger(TaskThread.class.getName());
	protected static final int IDLE = 0;
	protected static final int BUSY = 1;
	protected static final int CLOSED = 2;
	private int id;// 线程id
	private int status;// 0:idle,1:busy
	private boolean isRunning = true;

	public TaskThread(int id) {
		super();
		this.id = id;
		status = 0;
		logger.setLevel(Constants.log_level);
	}

	private Task task;

	public int getStatus() {
		return status;
	}

	public Task getTask() {
		return task;
	}

	public synchronized void startTask(Task task) {
		this.task = task;
		notify();
	}

	@Override
	public synchronized void run() {
		while (isRunning) {
			if (this.status == TaskThread.IDLE && this.task != null) {
				this.status = TaskThread.BUSY;
				logger.log(Level.INFO, "Thread " + id + "start task");
				this.task.work();

				this.status = TaskThread.IDLE;
				this.task = null;
			} else {
				try {
					wait();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 清出先撑资源，关闭线程
	 */
	public synchronized void release() {
		if (this.getStatus() != TaskThread.CLOSED) {
			isRunning = false;
			if (this.getStatus() == TaskThread.IDLE) {
				this.status = TaskThread.CLOSED;
				notify();

			}
		}

	}

}
