package org.enilu.socket.v3.server.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 数据库文件在内存中的映射对象
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class DmsFile {
	private String filename = "/media/data/dms.db";
	private File file;
	private FileChannel channel;
	private long size;
	private MappedByteBuffer mmap;
	private RandomAccessFile randomAccessFile;

	public DmsFile() throws IOException {
		file = new File(filename);
		if (file.exists()) {
			loadData();
		} else {
			init();
		}

	}

	private void loadData() throws IOException {
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = this.randomAccessFile.getChannel();
		size = channel.size();
		mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
	}

	private void init() throws IOException {
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = randomAccessFile.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(16);
		channel.write(buff, Segment.size);
	}

}
