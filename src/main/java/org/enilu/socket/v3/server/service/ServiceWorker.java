/**
 * 
 */
package org.enilu.socket.v3.server.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.model.MsgHeader;
import org.enilu.socket.v3.commons.model.MsgReplay;
import org.enilu.socket.v3.commons.model.MsgSender;
import org.enilu.socket.v3.commons.util.Constants;
import org.enilu.socket.v3.server.threadpool.Worker;

/**
 * 服务器端服务类
 * 
 * @author enilu
 * 
 */
public class ServiceWorker extends Worker {
	private static Logger logger = Logger.getLogger(ServiceWorker.class
			.getName());
	private final MsgSender sender;
	private final SocketChannel socket;

	public ServiceWorker(SocketChannel socket, MsgSender sender) {
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
