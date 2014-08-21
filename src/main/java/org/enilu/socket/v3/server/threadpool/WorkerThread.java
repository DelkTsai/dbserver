package org.enilu.socket.v3.server.threadpool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.Constants;

/**
 * 自定义的线程类，用作线程池李的标准线程对象
 * 
 * @author enilu
 * 
 */
public class WorkerThread extends Thread {
	private static Logger logger = Logger.getLogger(WorkerThread.class
			.getName());
	protected static final int IDLE = 0;
	protected static final int BUSY = 1;
	protected static final int CLOSED = 2;
	private int id;// 线程id
	private int status;// 0:idle,1:busy
	private boolean isRunning = true;

	public WorkerThread(int id) {
		super();
		this.id = id;
		status = 0;
		logger.setLevel(Constants.log_level);
	}

	private Worker worker;

	public int getStatus() {
		return status;
	}

	public Worker getWorker() {
		return worker;
	}

	public synchronized void startWorker(Worker worker) {
		this.worker = worker;
		notify();
	}

	@Override
	public synchronized void run() {
		while (isRunning) {
			if (this.status == WorkerThread.IDLE && this.worker != null) {
				this.status = WorkerThread.BUSY;
				logger.log(Level.INFO, "Thread " + id + "start work");
				this.worker.work();

				this.status = WorkerThread.IDLE;
				this.worker = null;
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
		if (this.getStatus() != WorkerThread.CLOSED) {
			isRunning = false;
			if (this.getStatus() == WorkerThread.IDLE) {
				this.status = WorkerThread.CLOSED;
				notify();

			}
		}

	}

}
