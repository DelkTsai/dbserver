package org.enilu.socket.v3.server.io;

/**
 * data page：the minimum unit of data storage
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Page {
	public static final int DEFAULT_SIZE = 4194304;
	/** page header details start: **/
	private String id = "PAGH";

	// flage of page:1:used 0:deleted
	private int flag;
	private int size;
	private int freeSpace;
	private int numSlots;
	private int slotOffset;
	private int freeOffset;
	/** page header details end: **/

	private int pageHeaderLength = 20;

	public void Page() {
		this.flag = 1;
		this.size = DEFAULT_SIZE;
		this.numSlots = 0;
		this.freeSpace = this.size - this.pageHeaderLength;
		this.slotOffset = 20;
		this.freeOffset = DEFAULT_SIZE;
	}

}
