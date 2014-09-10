package org.enilu.socket.v3.server.io;

/**
 * 数据库段
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Segment {

	public static int size = 134217728;
	/**
	 * 一个segment包含32个page
	 */
	private Page[] pages = new Page[32];

}
