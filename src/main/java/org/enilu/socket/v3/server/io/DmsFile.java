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
 * 该类是数据库文件在内存中的映射对象
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
	private int size;
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
		size = (int) channel.size();
		mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
		byte[] bytes = new byte[size];
		mmap.get(bytes, 0, size);

		db = new Database(bytes);

		// byte[] dmsHeader = new byte[Dms.DMS_FILE_HEADER_SIZE];
		// System.arraycopy(bytes, 0, dmsHeader, 0, Dms.DMS_FILE_HEADER_SIZE);
		// Database db = new Database(bytes);
		// byte[] pageHeader = new byte[Page.HEAD_SIZE];
		// System.arraycopy(bytes, Dms.DMS_FILE_HEADER_SIZE, pageHeader, 0,
		// Page.HEAD_SIZE);
		// Page page = new Page(pageHeader);
		logger.log(Level.INFO, "database header:" + db.toString());
		// logger.log(Level.INFO, "page header:" + page.toString());
	}

	private void init() throws Exception {
		logger.log(Level.INFO, "init database file");
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = randomAccessFile.getChannel();
		// 创建一个128mb的数据库文件
		ByteBuffer buff = ByteBuffer.allocate(1);
		channel.write(buff, Dms.DMS_FILE_SEGMENT_SIZE
				+ Dms.DMS_FILE_HEADER_SIZE - 1);
		size = (int) channel.size();
		// 将数据库头信息写入到数据库文件中
		db = new Database();
		mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0,
				Dms.DMS_FILE_SEGMENT_SIZE);
		byte[] bytes = new byte[Dms.DMS_FILE_HEADER_SIZE + Page.HEAD_SIZE];
		System.arraycopy(db.getHeader(), 0, bytes, 0, Dms.DMS_FILE_HEADER_SIZE);
		System.arraycopy(new Page().getHeader(), 0, bytes,
				Dms.DMS_FILE_HEADER_SIZE, Page.HEAD_SIZE);
		mmap.put(bytes, 0, Dms.DMS_FILE_HEADER_SIZE + Page.HEAD_SIZE);
	}

}
