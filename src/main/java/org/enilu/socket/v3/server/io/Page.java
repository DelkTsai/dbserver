package org.enilu.socket.v3.server.io;

import java.util.List;

import org.enilu.socket.v3.commons.util.ByteUtil;

/**
 * data page：the minimum unit of data storage
 * <p/>
 * 2014年9月10日
 * 
 * @author enilu(82552623@qq.com)
 */
public class Page {
	public static final int HEAD_SIZE = 24;
	/** page header details start: **/
	private String id;// segment index-page id

	// flage of page:1:used 0:deleted
	private int flag;
	private int size;
	private int freeSpace;
	private int numSlots;
	private int slotOffset;
	private int freeOffset;
	private List<byte[]> slots;// slot集合
	private List<byte[]> records;// 记录集合
	private byte[] pageByte;

	/** page header details end: **/

	public Page(byte[] bytes) {
		this.pageByte = bytes;
		byte[] flagByte = new byte[4];
		byte[] sizeByte = new byte[4];
		byte[] freeSpaceByte = new byte[4];
		byte[] numSlotsByte = new byte[4];
		byte[] slotOffsetByte = new byte[4];
		byte[] freeOffsetByte = new byte[4];

		System.arraycopy(bytes, 0, flagByte, 0, 4);
		System.arraycopy(bytes, 4, sizeByte, 0, 4);
		System.arraycopy(bytes, 8, freeSpaceByte, 0, 4);
		System.arraycopy(bytes, 12, numSlotsByte, 0, 4);
		System.arraycopy(bytes, 16, slotOffsetByte, 0, 4);
		System.arraycopy(bytes, 20, freeOffsetByte, 0, 4);

		this.flag = ByteUtil.byteToInt(flagByte);
		this.size = ByteUtil.byteToInt(sizeByte);
		this.freeSpace = ByteUtil.byteToInt(freeSpaceByte);
		this.numSlots = ByteUtil.byteToInt(numSlotsByte);
		this.slotOffset = ByteUtil.byteToInt(slotOffsetByte);
		this.freeOffset = ByteUtil.byteToInt(freeOffsetByte);

	}

	public Page() {
		this.flag = 1;
		this.size = Dms.DMS_PAGESIZE;
		this.numSlots = 0;
		this.freeSpace = this.size - HEAD_SIZE;
		this.slotOffset = HEAD_SIZE - 1;
		this.freeOffset = Dms.DMS_PAGESIZE;
	}

	public byte[] getHeader() {
		byte[] ret = new byte[HEAD_SIZE];
		byte[] flagByte = ByteUtil.intToByteArray(this.flag);
		byte[] sizeByte = ByteUtil.intToByteArray(this.size);
		byte[] freeSpaceByte = ByteUtil.intToByteArray(this.freeSpace);
		byte[] numSlotsByte = ByteUtil.intToByteArray(this.numSlots);
		byte[] slotOffsetByte = ByteUtil.intToByteArray(this.slotOffset);
		byte[] freeOffsetByte = ByteUtil.intToByteArray(this.freeOffset);

		System.arraycopy(flagByte, 0, ret, 0, 4);
		System.arraycopy(sizeByte, 0, ret, 4, 4);
		System.arraycopy(freeSpaceByte, 0, ret, 8, 4);
		System.arraycopy(numSlotsByte, 0, ret, 12, 4);
		System.arraycopy(slotOffsetByte, 0, ret, 16, 4);
		System.arraycopy(freeOffsetByte, 0, ret, 20, 4);
		return ret;
	}

	@Override
	public String toString() {
		return "id:" + this.id + "\tflag:" + this.flag + "\tsize:" + this.size
				+ "\tfreeSpace:" + this.freeSpace + "\tnumSloat:"
				+ this.numSlots + "\tslotOffset:" + this.slotOffset
				+ "\tfreeOffset:" + this.freeOffset;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int getFlag() {
		return flag;
	}

	public int getSize() {
		return size;
	}

	public int getFreeSpace() {
		return freeSpace;
	}

	public int getNumSlots() {
		return numSlots;
	}

	public int getSlotOffset() {
		return slotOffset;
	}

	public int getFreeOffset() {
		return freeOffset;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFreeSpace(int freeSpace) {
		this.freeSpace = freeSpace;
	}

	public void setNumSlots(int numSlots) {
		this.numSlots = numSlots;
	}

	public void setSlotOffset(int slotOffset) {
		this.slotOffset = slotOffset;
	}

	public void setFreeOffset(int freeOffset) {
		this.freeOffset = freeOffset;
	}

	public void addRecord(byte[] record) {
		this.records.add(record);
	}

	public void addSlot(byte[] slot) {
		this.slots.add(slot);
	}

	public byte[] getPageByte() {
		return pageByte;
	}

	public void setPageByte(byte[] pageByte) {
		this.pageByte = pageByte;
	}

}
