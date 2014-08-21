package org.enilu.socket.v3.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.server.threadpool.ThreadPool;
import org.enilu.socket.v3.server.threadpool.Worker;
import org.enilu.socket.v3.server.threadpool.WorkerQueue;
import org.enilu.socket.v3.server.threadpool.WorkerThread;

/**
 * 后台服务类，从worker队列中获取任务，并从线程池中获取线程进行处理
 * <p>
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class ServiceEngine {
	private static Logger logger = Logger.getLogger(ServiceEngine.class
			.getName());
	static ServiceEngine instance;
	static ThreadPool threadPool;
	private static boolean isRunning = false;

	private ServiceEngine() {

	}

	public static ServiceEngine getInstance() {
		if (instance == null) {
			isRunning = true;
			instance = new ServiceEngine();

			logger.log(Level.INFO, "bootstrap serviceEngine");
			threadPool = ThreadPool.getThreadPool(50);
			Task task = new Task();
			new Thread(task).start();

		}
		return instance;
	}

	/**
	 * 停止数据库服务
	 */
	public void shutdown() {
		while (isRunning) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		threadPool.shutdown();

	}

	/**
	 * 持续工作的线程，不停的从任务队列中获取任务，并从idle线程李表中获取空闲线程，使用idle线程来跑任务
	 * 
	 * @author enilu
	 * 
	 */
	static class Task implements Runnable {
		public Task() {
			super();
		}

		@Override
		public void run() {
			WorkerQueue workerQueue = WorkerQueue.getInstance();
			Worker worker = null;
			while ((worker = workerQueue.take()) != null) {
				// Worker worker = WorkerQueue.getInstance().take();
				if ("shutdown".equals(worker.toString())) {
					isRunning = false;
					break;
				}
				WorkerThread thread = threadPool.get();
				logger.log(Level.INFO, "start thread:" + thread.getId()
						+ " start a work");
				thread.startWorker(worker);
			}

		}

	}

}
