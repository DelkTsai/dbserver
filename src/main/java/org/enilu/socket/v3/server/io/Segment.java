package org.enilu.socket.v3.server.io;

/**
 * 
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Segment {
	/**
	 * the capacity of a segment is 128mb=134217728bytes
	 */
	public static int size = 134217728;//
	/**
	 * a segment contains 32 pages
	 */
	private Page[] pages = new Page[32];

}
