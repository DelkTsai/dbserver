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
	// private Map<String, Page> pages = new HashMap<String, Page>();
	private Page[] pages = new Page[32];
	private int index;

	public Segment(int index, byte[] bytes) {
		this.index = index;
		byte[] pageBytes = new byte[Dms.DMS_PAGESIZE];
		for (int i = 0; i < 32; i++) {
			System.arraycopy(bytes, i * Dms.DMS_PAGESIZE, pageBytes, 0,
					Dms.DMS_PAGESIZE);
			Page page = new Page(pageBytes);
			page.setId(index + "-" + i);
			pages[i] = page;
			// pages.put(new Date().getTime() + "" + i, new Page(pageBytes));
		}
	}

	public Segment() {
		super();
	}

	public Page[] getPages() {
		return pages;
	}

}
