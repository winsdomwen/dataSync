package com.gci.datax.engine.plugin;

import java.util.List;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.storage.Storage;

/**
 * {@link Reader} 写入数据到存储的缓冲区
 * 或者是{@link Writer}从存储读取数据的缓冲区
 * 
 * <p>
 * 该类有两面性, 当它与 reader共处, 它展示{@link LineSender}的一面.
 * 当它与Writer共处, 它展示{@link LineReceiver}的一面. 这两面都用缓冲机制.
 * </p>
 * 
 * */
public class BufferedLineExchanger implements LineSender, LineReceiver {

	static private final int DEFAUTL_BUF_SIZE = 64;

	/** {@link Reader}在StroeageForWrite区域存储数据. */
	private Line[] writeBuf;

	/**{@link Writer}从StroeageForRead区域获取的存储数据. */
	private Line[] readBuf;

	private int writeBufIdx = 0;

	private int readBufIdx = 0;

	private List<Storage> storageForWrite;

	private Storage storageForRead;

	/**
	 * 构造函数{@link BufferedLineExchanger}.
	 * 
	 * @param	storageForRead
	 * 			{@link Writer}获取数据的地方.
	 * 
	 * @param	storageForWrite
	 * 			{@link Reader}写入数据的地方.
	 * 
	 */
	public BufferedLineExchanger(Storage storageForRead, List<Storage> storageForWrite) {
		this(storageForRead, storageForWrite, DEFAUTL_BUF_SIZE);
	}

	/**
	 * 构造函数{@link BufferedLineExchanger}.
	 * 
	 * @param	storageForRead
	 * 			{@link Writer}获取数据的地方.
	 * 
	 * @param 	storageForWrite
	 * 			{@link Reader}写入数据的地方.
	 * 
	 * @param	bufSize
	 * 			存储缓冲区大小.
	 * 
	 */
	public BufferedLineExchanger(Storage storageForRead,
			List<Storage> storageForWrite, int bufSize) {
		this.storageForRead = storageForRead;
		this.storageForWrite = storageForWrite;
		this.writeBuf = new Line[bufSize];
		this.readBuf = new Line[bufSize];
	}

	@Override
	public Line getFromReader() {
		if (readBufIdx == 0) {
			readBufIdx = storageForRead.pull(readBuf);
			if (readBufIdx == 0) {
				return null;
			}
		}
		return readBuf[--readBufIdx];
	}

	@Override
	public Line createLine() {
		return new DefaultLine();
	}

	/**
	 * 放置一行{@link Line}到{@link Storage}.
	 * 
	 * @param 	line	
	 * 
	 * @return
	 *
	 * */
	@Override
	public boolean sendToWriter(Line line) {
		if (writeBufIdx >= writeBuf.length) {
			writeAllStorage(writeBuf, writeBufIdx);
			writeBufIdx = 0;
		}
		writeBuf[writeBufIdx++] = line;
		return true;
	}

	@Override
	public void flush() {
		if (writeBufIdx > 0) {
			writeAllStorage(writeBuf, writeBufIdx);
		}
	}

	/**
	 * test.
	 * 
	 * */
	@Override
	public boolean fakeSendToWriter(int lineLength) {
		for (Storage s : storageForWrite) {
			s.fakePush(lineLength);
		}
		return false;
	}

	/**
	 *写入缓冲的数据到所有存储(用一个Line数组)， 提供数据给{@link Writer}.
	 * 
	 * @param lines
	 * 
	 * @param size
	 * 
	 * @return
	 * 
	 */
	private boolean writeAllStorage(Line[] lines, int size) {
		for (Storage s : this.storageForWrite) {
			s.push(lines, size);
		}
		return true;
	}

}
