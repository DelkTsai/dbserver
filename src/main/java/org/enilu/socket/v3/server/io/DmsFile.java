package org.enilu.socket.v3.server.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.ByteUtil;
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
	private static DmsFile instance;

	private DmsFile() throws Exception {
		logger.setLevel(Constants.log_level);
		file = new File(filename);
		if (file.exists()) {
			loadData();
		} else {
			init();
		}

	}

	public static DmsFile getInstance() {
		if (instance == null) {
			try {
				instance = new DmsFile();
			} catch (Exception e) {
				logger.log(Level.FINE, "初始化存储系统异常");
				// e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * 加载数据库文件到内存中
	 * 
	 * @throws IOException
	 */
	private void loadData() throws IOException {
		logger.log(Level.INFO, "load file to memory");
		randomAccessFile = new RandomAccessFile(file, "rw");
		channel = this.randomAccessFile.getChannel();
		size = (int) channel.size();
		mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
		byte[] bytes = new byte[size];
		mmap.get(bytes, 0, size);
		db = new Database(bytes);

		logger.log(Level.INFO, "database header:" + db.toString());
	}

	/**
	 * 初始化数据库文件
	 * 
	 * @throws Exception
	 */
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
				Dms.DMS_FILE_HEADER_SIZE + Dms.DMS_FILE_SEGMENT_SIZE);
		byte[] bytes = new byte[Dms.DMS_FILE_HEADER_SIZE
				+ Dms.DMS_FILE_SEGMENT_SIZE];
		System.arraycopy(db.getHeader(), 0, bytes, 0, Dms.DMS_FILE_HEADER_SIZE);
		for (int i = 0; i < 32; i++) {
			byte[] tmp = new Page().getHeader();
			System.arraycopy(tmp, 0, bytes, Dms.DMS_FILE_HEADER_SIZE + i
					* Dms.DMS_PAGESIZE, Page.HEAD_SIZE);
		}
		mmap.put(bytes);
	}

	/**
	 * 扩展segment空间
	 */
	private void extendSegment() {

	}

	/**
	 * 根据需要的空间大小查找可用的Page
	 * 
	 * @param requiredSize
	 * @return
	 */
	public Page findPage(int requiredSize) {
		Segment[] segs = db.getSegments();
		Page result = null;
		for (Segment seg : segs) {
			Page[] pages = seg.getPages();
			for (Page page : pages) {
				if (page.getFreeSpace() > requiredSize) {
					result = page;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 插入一条记录
	 * 
	 * @param bson
	 * @return
	 */
	public int insert(byte[] content) {
		Page page = findPage(content.length + 4);
		if (page == null) {
			extendSegment();
		}
		int segmentIndex = Integer.valueOf(page.getId().split("-")[0]);
		int pageIndex = Integer.valueOf(page.getId().split("-")[1]);
		// 获取record的大小=size大小+content大小
		// 根据freeoffset，存放record[]，更新freeoffset
		// 添加slot：获取record位置，存放在最新的slot位置，并更新slotoffset+=4；
		// 更新slotnum+=1
		// 更新free space，总大小-head size-（slot num*4）-freeoffset

		byte[] msgLen = ByteUtil.intToByteArray(content.length);
		byte[] record = new byte[msgLen.length + content.length];
		System.arraycopy(msgLen, 0, record, 0, msgLen.length);
		System.arraycopy(content, 0, record, msgLen.length, content.length);
		int recordOffset = Dms.DMS_FILE_HEADER_SIZE + segmentIndex
				* Dms.DMS_FILE_SEGMENT_SIZE + Dms.DMS_PAGESIZE * pageIndex
				+ page.getFreeOffset() - record.length;
		mmap.put(record, recordOffset, record.length);
		page.setFreeOffset(page.getFreeOffset() - record.length);

		page.addSlot(ByteUtil.intToByteArray(record.length));
		page.setSlotOffset(page.getSlotOffset() + 4);
		page.setNumSlots(page.getNumSlots() + 1);
		page.setFreeSpace(page.getFreeSpace() - record.length - 4);

		return 0;
	}

}
