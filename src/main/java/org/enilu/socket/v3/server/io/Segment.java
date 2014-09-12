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
	 * a segment contains 32 pages
	 */
	private Page[] pages = new Page[32];

	public Segment(byte[] bytes) {

	}

	public Segment() {
		super();
	}

}
