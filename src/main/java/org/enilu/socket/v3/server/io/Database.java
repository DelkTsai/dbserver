package org.enilu.socket.v3.server.io;

import java.io.IOException;
import java.util.logging.Logger;

import org.enilu.socket.v3.commons.util.ByteUtil;
import org.enilu.socket.v3.commons.util.Constants;

/**
 * 一个应用只有一个Database实例，其是数据库文件在内存中的映射对象
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Database {
	private static Logger logger = Logger.getLogger(Database.class.getName());
	private final static int VERSION = 0;
	private Segment[] segs = new Segment[15];

	/** database header start **/
	private String id = "DMSH";
	// page count
	private int size;
	// 1:used 0:deleted
	private int flag;
	private int version;

	/** database header end **/

	public Database() {
		logger.setLevel(Constants.log_level);
		this.size = 480;
		this.flag = 0;
		this.version = VERSION;
	}

	public String getId() {
		return id;
	}

	public Database(byte[] bytes) throws IOException {
		byte[] dmsHeader = new byte[Dms.DMS_FILE_HEADER_SIZE];
		System.arraycopy(bytes, 0, dmsHeader, 0, Dms.DMS_FILE_HEADER_SIZE);
		byte[] idbyte = new byte[4];
		byte[] sizebyte = new byte[4];
		byte[] flagbyte = new byte[4];
		byte[] versionbyte = new byte[4];
		System.arraycopy(dmsHeader, 0, idbyte, 0, 4);
		System.arraycopy(dmsHeader, 4, sizebyte, 0, 4);
		System.arraycopy(dmsHeader, 8, flagbyte, 0, 4);
		System.arraycopy(dmsHeader, 12, versionbyte, 0, 4);
		this.id = new String(idbyte);
		this.size = ByteUtil.byteToInt(sizebyte);
		this.flag = ByteUtil.byteToInt(flagbyte);
		this.version = ByteUtil.byteToInt(versionbyte);
		byte[] tmp = new byte[bytes.length - Dms.DMS_FILE_HEADER_SIZE];
		System.arraycopy(bytes, Dms.DMS_FILE_HEADER_SIZE, tmp, 0, tmp.length);
		loadSegment(tmp);
	}

	/**
	 * 将数据库文件映射为Segment
	 * 
	 * @param bytes
	 */
	private void loadSegment(byte[] bytes) {
		int segmentCount = bytes.length / Dms.DMS_FILE_SEGMENT_SIZE;

		byte[] segmentbyte = new byte[Dms.DMS_FILE_SEGMENT_SIZE];
		for (int i = 0; i < segmentCount; i++) {
			System.arraycopy(bytes, i * Dms.DMS_FILE_SEGMENT_SIZE, segmentbyte,
					0, Dms.DMS_FILE_SEGMENT_SIZE);
			segs[i] = new Segment(i, segmentbyte);
		}
	}

	public byte[] getHeader() throws Exception {

		byte[] ret = new byte[Dms.DMS_FILE_HEADER_SIZE];

		byte[] sizebyte = ByteUtil.intToByteArray(this.size);
		byte[] flagbyte = ByteUtil.intToByteArray(this.flag);
		byte[] versionbyte = ByteUtil.intToByteArray(this.version);
		System.arraycopy(id.getBytes(), 0, ret, 0, 4);
		System.arraycopy(sizebyte, 0, ret, 4, sizebyte.length);
		System.arraycopy(flagbyte, 0, ret, 4 + sizebyte.length, flagbyte.length);
		System.arraycopy(versionbyte, 0, ret, 4 + sizebyte.length
				+ flagbyte.length, versionbyte.length);

		return ret;
	}

	@Override
	public String toString() {

		return "id:" + this.id + "\tflag:" + this.flag + "\tsize:" + this.size
				+ "\tversion:" + this.version;
	}

	public Segment[] getSegments() {
		return segs;
	}

}
