/**
 * 
 */
package org.enilu.socket.v3.server.threadpool.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.model.MsgHeader;
import org.enilu.socket.v3.commons.model.MsgReplay;
import org.enilu.socket.v3.commons.model.MsgSender;
import org.enilu.socket.v3.commons.util.Constants;
import org.enilu.socket.v3.server.service.DeleteService;
import org.enilu.socket.v3.server.service.InsertService;
import org.enilu.socket.v3.server.service.QueryService;
import org.enilu.socket.v3.server.service.UpdateService;

/**
 * 处理客户请求的任务类
 * <p>
 * <li>根据客户请求类型调用不同的service来进行业务处理</li>
 * <li>获取service的处理结果返回给客户端</li>
 * 
 * @author enilu(82552623@qq.com)
 * 
 */
public class ServiceTask extends Task {
	private static Logger logger = Logger
			.getLogger(ServiceTask.class.getName());
	private final MsgSender sender;
	private final SocketChannel socket;

	public ServiceTask(SocketChannel socket, MsgSender sender) {
		logger.setLevel(Constants.log_level);
		this.sender = sender;
		this.socket = socket;
	}

	@Override
	public String work() {
		logger.log(Level.INFO, "accept from client："
				+ sender.getHeader().getSequence());
		int opCode = sender.getHeader().getOpCode();
		MsgReplay replay = null;
		switch (opCode) {
		case MsgHeader.OP_INSERT:
			replay = new InsertService().process(sender);
			break;
		case MsgHeader.OP_DELETE:
			replay = new DeleteService().process(sender);
			break;
		case MsgHeader.OP_UPDATE:
			replay = new UpdateService().process(sender);
			break;
		case MsgHeader.OP_QUERY:
			replay = new QueryService().process(sender);
			break;
		default:
			break;
		}
		ByteBuffer bufferSender = ByteBuffer.wrap(replay.getBytes());
		try {
			logger.log(Level.INFO, "send message to client："
					+ replay.getHeader().getSequence());
			socket.write(bufferSender);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
