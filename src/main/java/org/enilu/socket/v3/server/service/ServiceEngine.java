package org.enilu.socket.v3.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.server.threadpool.TaskQueue;
import org.enilu.socket.v3.server.threadpool.TaskThread;
import org.enilu.socket.v3.server.threadpool.ThreadPool;
import org.enilu.socket.v3.server.threadpool.task.Task;

/**
 * 后台服务类，从task队列中获取任务，并从线程池中获取线程进行处理
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
			Job job = new Job();
			new Thread(job).start();

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
	static class Job implements Runnable {
		public Job() {
			super();
		}

		@Override
		public void run() {
			TaskQueue taskQueue = TaskQueue.getInstance();
			Task task = null;
			while ((task = taskQueue.take()) != null) {
				if ("shutdown".equals(task.toString())) {
					isRunning = false;
					break;
				}
				TaskThread thread = threadPool.get();
				logger.log(Level.INFO, "thread:" + thread.getId()
						+ " start a task");
				thread.startTask(task);
			}

		}

	}

}
