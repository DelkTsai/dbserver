import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * java mmap测试
 * <p/>
 * 2014年9月9日
 * 
 * @author enilu(82552623@qq.com)
 */
public class MmapTest {
	public static void main(String[] args) throws Exception {
		FileChannel channel = new RandomAccessFile(new File(
				"/media/data/test.txt"), "rw").getChannel();
		System.out.println(channel.size());
		MappedByteBuffer mmap = channel.map(FileChannel.MapMode.READ_WRITE, 0,
				channel.size());
		int ret = mmap.getInt();
		System.out.println(ret);
		// ByteBuffer buff = ByteBuffer.allocate(100);
		// buff.put("hello world".getBytes());
		// buff.flip();
		// channel.write(buff, 8192);
		//
		// byte b = 53;
		// mmap.put(10, b);

	}
}
