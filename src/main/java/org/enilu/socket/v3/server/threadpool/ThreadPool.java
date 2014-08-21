package org.enilu.socket.v3.server.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.Constants;

/**
 * 线程池
 * 
 * @author enilu
 * 
 */
public class ThreadPool {

	private static Logger logger = Logger.getLogger(ThreadPool.class.getName());
	private static ThreadPool instance;
	private static List<WorkerThread> pool = new ArrayList<WorkerThread>();// 线程队列
	private static int threadNum = 10;

	private ThreadPool() {
		logger.setLevel(Constants.log_level);
	}

	/**
	 * 根据制定的线程数量构造线程池
	 * 
	 * @param threadNum
	 * @return
	 */
	public static ThreadPool getThreadPool(int num) {
		if (instance == null) {
			logger.log(Level.INFO, "create " + num
					+ " threads add to threadPool");
			instance = new ThreadPool();
			threadNum = num;
			for (int i = 0; i < num; i++) {
				WorkerThread wt = new WorkerThread(i);
				wt.start();
				pool.add(wt);
			}

		}
		int xiangchaNum = num > threadNum ? (num - threadNum) : 0;
		for (int i = 0; i < xiangchaNum; i++) {
			WorkerThread wt = new WorkerThread(i);
			wt.start();
			pool.add(wt);
		}
		return instance;

	}

	/**
	 * 使用默认的线程数目构造线程池
	 * 
	 * @return
	 */
	public static ThreadPool getTheadPool() {
		return getThreadPool(50);// 默认生成50个线程
	}

	public WorkerThread get() {
		synchronized (this) {
			WorkerThread thread = null;
			while (thread == null) {
				for (WorkerThread t : pool) {
					if (t.getStatus() == WorkerThread.IDLE) {
						thread = t;
					}
				}
			}
			return thread;
		}

	}

	public int getIdleCount() {
		int idleCount = 0;
		for (WorkerThread t : pool) {
			if (t.getStatus() == WorkerThread.IDLE) {
				idleCount++;
			}
		}
		return idleCount;
	}

	public int getBusyCount() {
		int busyCount = 0;
		for (WorkerThread t : pool) {
			if (t.getStatus() == WorkerThread.BUSY) {
				busyCount++;
			}
		}
		return busyCount;
	}

	public int getClosedCount() {

		int count = 0;
		for (WorkerThread t : pool) {
			if (t.getStatus() == WorkerThread.CLOSED) {
				count++;
			}
		}
		return count;

	}

	/**
	 * 
	 */
	public void shutdown() {
		logger.log(Level.INFO, "threadPool shutdown...");
		int workSize = WorkerQueue.getInstance().getWorkSize();
		while (workSize > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			workSize = WorkerQueue.getInstance().getWorkSize();
		}
		while (this.getClosedCount() < threadNum) {
			for (WorkerThread t : pool) {
				if (t.getStatus() == WorkerThread.IDLE) {
					t.release();
				}
			}
		}
		logger.log(Level.INFO, "threadPool shutdown ok");

	}
}
