package org.enilu.socket.v3.server.threadpool;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.enilu.socket.v3.server.threadpool.task.Task;

/**
 * 消息队列工具类,包含对task队列的简单操作
 * <p>
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class TaskQueue {
	private static BlockingDeque<Task> taskList = null;
	static TaskQueue instance;

	private TaskQueue() {

	}

	public static TaskQueue getInstance() {
		if (instance == null) {
			instance = new TaskQueue();
			taskList = new LinkedBlockingDeque<Task>(50);
		}
		return instance;
	}

	public void push(Task task) {
		taskList.offer(task);

	}

	public Task take() {

		try {
			return taskList.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 */
	public int getTaskSize() {
		return taskList.size();
	}
}
