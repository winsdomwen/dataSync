package com.gci.datax.engine.storage;

import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;


/**
 * 一个具体的存储类，用RAM内存空间来存储交换区
 * 它提供高速、安全的方式来实现数据交换
 * 
 *@see {@link Storage}
 *@see {@link DoubleQueue}
 *@see {@link BufferedLineExchanger}
 */
public class RAMStorage extends Storage {
	private static final Logger log = Logger.getLogger(RAMStorage.class);

	private DoubleQueue mars = null;

	private final int waitTime = 3000; 

	public boolean init(String id, int lineLimit, int byteLimit,
			int destructLimit) {
		if (super.init(id, lineLimit, byteLimit, destructLimit) == false){
			return false;
		}
		this.mars = new DoubleQueue(lineLimit, byteLimit);
		return true;
	}

	
	/**
	 * {@link Reader}写入一行记录到{@link Storage}, 
	 * 
	 * @param 	line
	 * 			一行记录 {@link Line}
	 * 
	 * @return
	 * 			true or false
	 * 
	 * */
	@Override
	public boolean push(Line line) {
		if (isPushClosed())
			return false;
		try {
			while (mars.offer(line, waitTime, TimeUnit.MILLISECONDS) == false) {
				getStat().incLineRRefused(1);
			}
		} catch (InterruptedException e) {
			return false;
		}
		getStat().incLineRx(1);
		getStat().incByteRx(line.length());
		return true;
	}


	/**
	 * {@link Reader}写入多行记录到{@link Storage}
	 * 
	 * @param 	lines
	 * 			多行
	 * 
	 * @param 	size
	 * 			行数
	 * 
	 * @return
	 * 			true or false
	 * 
	 * */
	@Override
	public boolean push(Line[] lines, int size) {
		if (isPushClosed()) {
			return false;
		}

		try {
			while (mars.offer(lines, size, waitTime, TimeUnit.MILLISECONDS) == false) {
			
				getStat().incLineRRefused(1);
			
                if (getDestructLimit() > 0 && getStat().getLineRRefused() >= getDestructLimit()){
                	if (!isPushClosed()){
                		log.warn("关闭RAMStorage:" + getStat().getId() + ". Queue:" + info() + " 超时:" + getStat().getLineRRefused());
                        setPushClosed(true);
                	}
                    return false;
                }
			
			}
		} catch (InterruptedException e) {
			return false;
		}

		getStat().incLineRx(size);
		for (int i = 0; i < size; i++) {
			getStat().incByteRx(lines[i].length());
		}

		return true;
	}

	@Override
	public boolean fakePush(int lineLength) {
		getStat().incLineRx(1);
		getStat().incByteRx(lineLength);
		return false;
	}

	/**
	 * {@link Writer}从{@link Storage}获取一行记录
	 * 
	 * @return 
	 * 			
	 * 
	 * */
	@Override
	public Line pull() {
		Line line = null;
		try {
			while ((line = mars.poll(waitTime, TimeUnit.MILLISECONDS)) == null) {
				getStat().incLineTRefused(1);
			}
		} catch (InterruptedException e) {
			return null;
		}
		if (line != null) {
			getStat().incLineTx(1);
			getStat().incByteTx(line.length());
		}
		return line;
	}

	/**
	 *{@link Writer}从{@link Storage}获取多行记录
	 * 
	 * @param	lines
	 * 			
	 * 
	 * @return
	 * 			获取的行数
	 * 
	 * */
	@Override
	public int pull(Line[] lines) {
		int readNum = 0;
		try {
			while ((readNum = mars.poll(lines, waitTime, TimeUnit.MILLISECONDS)) == 0) {
				getStat().incLineTRefused(1);
			}
		} catch (InterruptedException e) {
			return 0;
		}
		if (readNum > 0) {
			getStat().incLineTx(readNum);
			for (int i = 0; i < readNum; i++) {
				getStat().incByteTx(lines[i].length());
			}
		}
		if (readNum == -1) {
			return 0;
		}
		return readNum;
	}


	@Override
	public int size() {
		return mars.size();
	}


	@Override
	public boolean empty() {
		return (size() <= 0);
	}


	@Override
	public int getLineLimit() {
		return mars.getLineLimit();
	}


	@Override
	public String info() {
		return mars.info();
	}


	@Override
	public void setPushClosed(boolean close) {
		super.setPushClosed(close);
		mars.close();
	}

}
