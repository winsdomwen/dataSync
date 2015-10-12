package com.gci.datax.engine.plugin;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.storage.Storage;

/**
 * {@link Reader} 写入数据到存储的缓冲区
 * 或者是{@link Writer}从存储读取数据的缓冲区
 * 没有用到该类,每次onew Line效率不高
 * 
 * @see BufferedLineExchanger
 * 
 * */
public class LineExchanger implements LineSender, LineReceiver {

	private Storage storage;

	public LineExchanger(Storage storage) {
		this.storage = storage;
	}

	@Override
	public Line getFromReader() {
		return storage.pull();
	}

	@Override
	public Line createLine() {
		return new DefaultLine();
	}

	@Override
	public boolean sendToWriter(Line line) {
		return storage.push(line);
	}


	@Override
	public boolean fakeSendToWriter(int lineLength) {
		storage.fakePush(lineLength);
		return false;
	}


	@Override
	public void flush() {
    }
}
