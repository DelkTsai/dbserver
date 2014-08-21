package org.enilu.socket.v3.server.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.Constants;

/**
 * 线程池管理类
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class ThreadPool {

	private static Logger logger = Logger.getLogger(ThreadPool.class.getName());
	private static ThreadPool instance;
	private static List<TaskThread> pool = new ArrayList<TaskThread>();// 线程队列
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
				TaskThread wt = new TaskThread(i);
				wt.start();
				pool.add(wt);
			}

		}
		int xiangchaNum = num > threadNum ? (num - threadNum) : 0;
		for (int i = 0; i < xiangchaNum; i++) {
			TaskThread wt = new TaskThread(i);
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

	public TaskThread get() {
		synchronized (this) {
			TaskThread thread = null;
			while (thread == null) {
				for (TaskThread t : pool) {
					if (t.getStatus() == TaskThread.IDLE) {
						thread = t;
					}
				}
			}
			return thread;
		}

	}

	public int getIdleCount() {
		int idleCount = 0;
		for (TaskThread t : pool) {
			if (t.getStatus() == TaskThread.IDLE) {
				idleCount++;
			}
		}
		return idleCount;
	}

	public int getBusyCount() {
		int busyCount = 0;
		for (TaskThread t : pool) {
			if (t.getStatus() == TaskThread.BUSY) {
				busyCount++;
			}
		}
		return busyCount;
	}

	public int getClosedCount() {

		int count = 0;
		for (TaskThread t : pool) {
			if (t.getStatus() == TaskThread.CLOSED) {
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
		int taskSize = TaskQueue.getInstance().getTaskSize();
		while (taskSize > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			taskSize = TaskQueue.getInstance().getTaskSize();
		}
		while (this.getClosedCount() < threadNum) {
			for (TaskThread t : pool) {
				if (t.getStatus() == TaskThread.IDLE) {
					t.release();
				}
			}
		}
		logger.log(Level.INFO, "threadPool shutdown ok");

	}
}
