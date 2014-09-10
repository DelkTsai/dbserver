package org.enilu.socket.v3.server.io;


/**
 * 一个应用只有一个Database实例，其是数据库文件在内存中的映射对象
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Database {

	private Segment[] segs = new Segment[15];

	private String version;
	private int fileflag;
	private int flag;
	/**
	 * page count
	 */
	private int size;

}
