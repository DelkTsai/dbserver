package org.enilu.socket.v3.server.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.Constants;

/**
 * 数据库文件在内存中的映射对象
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class DmsFile {
	private static Logger logger = Logger.getLogger(DmsFile.class.getName());
	private String filename = "/media/data/dms.db";
	private File file;
	private FileChannel channel;
	private long size;
	private MappedByteBuffer mmap;
	private RandomAccessFile randomAccessFile;
	private Database db;

	public DmsFile() throws Exception {
		logger.setLevel(Constants.log_level);
		file = new File(filename);
		if (file.exists()) {
			loadData();
		} else {
			init();
		}

	}

	private void loadData() throws IOException {
		logger.log(Level.INFO, "load file to memory");
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = this.randomAccessFile.getChannel();
		size = channel.size();
		mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
		byte[] header = new byte[16];
		mmap.get(header, 0, 16);
		Database db = new Database(header);
		logger.log(Level.INFO, "database header:" + db.toString());
	}

	private void init() throws Exception {
		logger.log(Level.INFO, "init database file");
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = randomAccessFile.getChannel();
		// 将数据库头信息写入到文件中
		ByteBuffer dmsHeader = ByteBuffer.allocate(16);
		db = new Database();
		dmsHeader.put(db.getHeader(), 0, 16);
		dmsHeader.flip();
		channel.write(dmsHeader, 0);
		/**
		 * 通过在文件指定位置写入一个空字符占位来指定初始化文件大小
		 */
		ByteBuffer buff = ByteBuffer.allocate(1);
		channel.write(buff, Segment.size - 1);
	}

}
