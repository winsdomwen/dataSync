package com.gci.datax.engine.storage;

import com.gci.datax.common.plugin.Line;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 * 一个非常重要的类,表示一个区域有两块交换空间,一个是从数据源获取数据去存储,
 * 另外一个将存储的数据转移到目的地.
 * <p>
 * 一个经典的双队列, 开始时, 空间A和空间B都为空,加载任务开始加载数据到空间A,
 * 当空间A满了,就让数据源的数据加载到空间B,然后转储任务开始从空间A转储数据到目标数据源.
 * 如果空间A为空，为加载和转储任务交换AB两个空间。重复以上的操作。
 * 
 * </p>
 * 
 */
public class DoubleQueue extends AbstractQueue<Line> implements
		BlockingQueue<Line>, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private int lineLimit;

	private final Line[] itemsA;
	
	private final Line[] itemsB;

	private ReentrantLock readLock, writeLock;
	
	private Condition notEmpty;
	
	private Condition notFull;
	
	private Condition awake;

	/**
	 * writeArray : 从Reader的角度，reader从数据源获取数据且写入数据到line数组。
	 * readArray :  从Writer的角度，writer从这个line数组转储数据到目标数据源。
	 * 
	 * 因这双队列机制,在合适的时机,这俩line数组会交换。
	 * 
	 */
	private Line[] writeArray, readArray;
	
	private volatile int writeCount, readCount;
	
	private int writeArrayHP, writeArrayTP, readArrayHP, readArrayTP;
	
	private int byteLimit;
	
	private boolean closed = false;
	
	private int spillSize = 0;

	private long lineRx = 0;
	
	private long lineTx = 0;
	
	/**	收到来自数据源的数据的字节数(eg:httpreader load data from httpurl) */
	private long byteRx = 0;

	/**
	 * 从{@link DoubleQueue}空间获取line的数目信息. 
	 * 
	 * @return
	 * 			行数信息.
	 * 
	 */
	public String info() {
		//return String.format("(缓冲区)插入行数：%d,提取行数：%d", lineRx,lineTx);
		return lineRx + ":" + lineTx;
	}

	/**
	 * 构造函数
	 * 
	 * @param	lineLimit
	 * 
	 * @param	byteLimit			
	 * 
	 */
	public DoubleQueue(int lineLimit, int byteLimit) {
		if (lineLimit <= 0 || byteLimit <= 0) {
			throw new IllegalArgumentException(
					"Queue initial capacity can't less than 0!");
		}
		this.lineLimit = lineLimit;
		this.byteLimit = byteLimit;
		itemsA = new Line[lineLimit];
		itemsB = new Line[lineLimit];

		readLock = new ReentrantLock();
		writeLock = new ReentrantLock();

		notEmpty = readLock.newCondition();
		notFull = writeLock.newCondition();
		awake = writeLock.newCondition();

		readArray = itemsA;
		writeArray = itemsB;
		spillSize = lineLimit * 8 / 10;
	}

	public int getLineLimit() {
		return lineLimit;
	}

	public void setLineLimit(int capacity) {
		this.lineLimit = capacity;
	}

	/**
	 * 插入一条记录到缓冲交换数据的空间.
	 * 
	 * @param	line
	 * 
	 */
	private void insert(Line line) {
		writeArray[writeArrayTP] = line;
		++writeArrayTP;
		++writeCount;
		++lineRx;
		byteRx += line.length();
	}

	/**
	 * 插入一数组记录到缓冲交换数据的空间
	 * @param lines
	 * 
	 * @param size
	 * 
	 */
	private void insert(Line[] lines, int size) {
		for (int i = 0; i < size; ++i) {
			writeArray[writeArrayTP] = lines[i];
			++writeArrayTP;
			++writeCount;
			++lineRx;
			byteRx += lines[i].length();
		}
	}

	/**
	 * 从包含当前数据的空间提取一条记录。
	 * 
	 * @return	line
	 * 
	 */
	private Line extract() {
		Line e = readArray[readArrayHP];
		readArray[readArrayHP] = null;
		++readArrayHP;
		--readCount;
		++lineTx;
		return e;
	}


	/**
	 * 从包含当前数据的空间提取一数组记录
	 * 
	 * @param ea
	 * 			提取的记录数
	 * 
	 */
	private int extract(Line[] ea) {
		int readsize = Math.min(ea.length, readCount);
		for (int i = 0; i < readsize; ++i) {
			ea[i] = readArray[readArrayHP];
			readArray[readArrayHP] = null;
			++readArrayHP;
			--readCount;
			++lineTx;
		}
		return readsize;
	}

	/**
	 * 交换条件: 读取队列为空  && 写入队列不为空.
	 * 注意：该函数只在readLock被捕获到后才会被调用,否则会造成死锁
	 * cause dead lock.
	 * 
	 * @param	timeout
	 * 
	 * @param	isInfinite
	 *          是否需要一直等待，直到其它线程唤醒它
	 *          
	 * @return
	 * 
	 * @throws InterruptedException
	 * 
	 */

	private long queueSwitch(long timeout, boolean isInfinite)
			throws InterruptedException {
		writeLock.lock();
		try {
			if (writeCount <= 0) {
				if (closed) {
					return -2;
				}
				try {
					if (isInfinite && timeout <= 0) {
						awake.await();
						return -1;
					} else {
						return awake.awaitNanos(timeout);
					}
				} catch (InterruptedException ie) {
					awake.signal();
					throw ie;
				}
			} else {
				Line[] tmpArray = readArray;
				readArray = writeArray;
				writeArray = tmpArray;

				readCount = writeCount;
				readArrayHP = 0;
				readArrayTP = writeArrayTP;

				writeCount = 0;
				writeArrayHP = readArrayHP;
				writeArrayTP = 0;

				notFull.signal();
				// logger.debug("Queue switch successfully!");
				return -1;
			}
		} finally {
			writeLock.unlock();
		}
	}

	
	/**
	 * 如果存在写空间，将返回true，写入一行记录到空间
	 * 否则，它将在指定的时间尝试去做，如果超时时仍然失败，将返回false
	 * 
	 * @param	line
	 * 			一行.
	 * 
	 * @param	timeout
	 * 			指定限定时间
	 * 
	 * @param	unit
	 * 			时间单位
	 * 
	 * @return
	 * 			true or false
	 * 
	 */
	public boolean offer(Line line, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (line == null) {
			throw new NullPointerException();
		}
		long nanoTime = unit.toNanos(timeout);
		writeLock.lockInterruptibly();
		try {
			for (;;) {
				if (writeCount < writeArray.length) {
					insert(line);
					if (writeCount == 1) {
						awake.signal();
					}
					return true;
				}

				// 超时
				if (nanoTime <= 0) {
					return false;
				}
				// 一直等待
				try {
					nanoTime = notFull.awaitNanos(nanoTime);
				} catch (InterruptedException ie) {
					notFull.signal();
					throw ie;
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 如果存在写空间，将返回true，写入一数组记录到空间
	 * 否则，它将在指定的时间尝试去做，如果超时时仍然失败，将返回false
	 * 
	 * @param	lines
	 * 			line数组
	 * 
	 * @param	size
	 * 			行数
	 * 
	 * @param	timeout
	 * 			指定限定时间
	 * 
	 * @param	unit
	 * 			时间单位
	 * 
	 * @return
	 * 			true or false.
	 * 
	 * @throws	InterruptedException
	 * 
	 */
	public boolean offer(Line[] lines, int size, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (lines == null) {
			throw new NullPointerException();
		}
		long nanoTime = unit.toNanos(timeout);
		writeLock.lockInterruptibly();
		try {
			for (;;) {
				if (writeCount + size <= writeArray.length) {
					insert(lines, size);
					if (writeCount >= spillSize) {
						awake.signalAll();
					}
					return true;
				}

				// Time out
				if (nanoTime <= 0) {
					return false;
				}
				// keep waiting
				try {
					nanoTime = notFull.awaitNanos(nanoTime);
				} catch (InterruptedException ie) {
					notFull.signal();
					throw ie;
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 关闭同步锁和一个内部状态.
	 * 
	 */
	public void close() {
		writeLock.lock();
		try {
			closed = true;
			awake.signalAll();
		} finally {
			writeLock.unlock();
		}
	}

	
	/**
	 * 
	 * 
	 * @param	timeout
	 * 			指定限定时间
	 * 
	 * @param	unit
	 * 			时间单位
	 */
	public Line poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanoTime = unit.toNanos(timeout);
		readLock.lockInterruptibly();

		try {
			for (;;) {
				if (readCount > 0) {
					return extract();
				}

				if (nanoTime <= 0) {
					return null;
				}
				nanoTime = queueSwitch(nanoTime, true);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @param ea  
	 *
	 * 
	 * @param	timeout	
	 * 
	 * @param	unit
	 * 
	 * @return
	 * 			数据的行数.如果小于等于0,表明失败
	 * 
	 * @throws	InterruptedException
	 */
	public int poll(Line[] ea, long timeout, TimeUnit unit)
			throws InterruptedException {
		long nanoTime = unit.toNanos(timeout);
		readLock.lockInterruptibly();

		try {
			for (;;) {
				if (readCount > 0) {
					return extract(ea);
				}

				if (nanoTime == -2) {
					return -1;
				}

				if (nanoTime <= 0) {
					return 0;
				}
				nanoTime = queueSwitch(nanoTime, false);
			}
		} finally {
			readLock.unlock();
		}
	}

	public Iterator<Line> iterator() {
		return null;
	}

	/**
	 * 
	 * @return
	 * 
	 * */
	@Override
	public int size() {
		return (writeCount + readCount);
	}

	@Override
	public int drainTo(Collection<? super Line> c) {
		return 0;
	}

	@Override
	public int drainTo(Collection<? super Line> c, int maxElements) {
		return 0;
	}

	/**

	 * 
	 * @param	line
	 * 
	 * @see DoubleQueue#offer(Line, long, TimeUnit)
	 * 			
	 */
	@Override
	public boolean offer(Line line) {
		try {
			return offer(line, 20, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	@Override
	public void put(Line e) throws InterruptedException {
	}

	@Override
	public int remainingCapacity() {
		return 0;
	}

	@Override
	public Line take() throws InterruptedException {
		return null;
	}

	@Override
	public Line peek() {
		return null;
	}

	@Override
	public Line poll() {
		try {
			return poll(20, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
