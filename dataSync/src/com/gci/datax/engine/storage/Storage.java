package com.gci.datax.engine.storage;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.schedule.Reporter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link Reader}和{@link Writer}交换数据的缓冲区
 * 
 * */
public abstract class Storage {

	private int destructLimit = 0;
	
	/**
	 * Initialization for {@link Storage}.
	 * 
	 * @param	id
	 *          {@link Writer} id.
	 * 
	 * @param	lineLimit
	 *          Limit of the line number the {@link Storage} can hold.
	 * 
	 * @param	byteLimit
	 *          Limit of the bytes the {@link Storage} can hold.
	 * 
	 * @param destructLimit
        *
        * @return
	 * 			true for OK, false for failed.
	 * 
	 * */
	public boolean init(String id, int lineLimit, int byteLimit,
			int destructLimit) {
		if (this.stat == null) {
			this.stat = new Statistics(id, this);
		}
		this.stat.periodPass();
		pushClosed = false;
		if (lineLimit <= 0 || byteLimit <= 0) {
			return false;
		}
		
		this.destructLimit = destructLimit;
		
		return true;
	}
	
	public int getDestructLimit(){
		return destructLimit;
	}

	abstract public String info();

	abstract public boolean push(Line line);

	abstract public boolean push(Line[] lines, int size);

	/**
	 * For test.
	 * 
	 * */
	abstract public boolean fakePush(int lineLength);

	abstract public Line pull();

	abstract public int pull(Line[] lines);

	public boolean isPushClosed() {
		return pushClosed;
	}

	public void setPushClosed(boolean close) {
		pushClosed = close;
		return;
	}

	abstract public int size();

	abstract public boolean empty();

	abstract public int getLineLimit();

	public Statistics getStat() {
		return this.stat;
	}

	private Statistics stat;

	private boolean pushClosed;

	public class Statistics {
		private String id;

		private Date beginTime;

		private Date endTime;

		private long lineRx;

		private long lineTx;

		private long byteRx;

		private long byteTx;

		private long lineRRefused;

		private long lineTRefused;

		private long periodInSeconds;

		private long lineRxTotal;

		private long lineTxTotal;

		private long byteRxTotal;

		private long byteTxTotal;

		private long totalSeconds;

		private Storage storage;

		public Statistics(String id, Storage storage) {
			this.storage = storage;
			this.setId(id);
			lineRx = 0;
			lineTx = 0;
			byteRx = 0;
			byteTx = 0;
			lineRRefused = 0;
			lineTRefused = 0;
			lineRxTotal = 0;
			lineTxTotal = 0;
			byteRxTotal = 0;
			byteTxTotal = 0;
			totalSeconds = 0;
			beginTime = new Date();
		}

		public void periodPass() {
			lineRx = 0;
			lineTx = 0;
			byteRx = 0;
			byteTx = 0;
			// lineRRefused = 0;
			// lineTRefused = 0;
			totalSeconds += periodInSeconds;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getLineRx() {
			return lineRx;
		}

		public void incLineRx(long i) {
			this.lineRx += i;
			this.incLineRxTotal(i);
		}

		public long getLineTx() {
			return lineTx;
		}

		public void incLineTx(long i) {
			this.lineTx += i;
			this.incLineTxTotal(i);
		}

		public long getByteRx() {
			return byteRx;
		}

		public void incByteRx(long i) {
			this.byteRx += i;
			this.incByteRxTotal(i);
		}

		public long getByteTx() {
			return byteTx;
		}

		public void incByteTx(long i) {
			this.byteTx += i;
			this.incByteTxTotal(i);
		}

		public long getLineRRefused() {
			return lineRRefused;
		}

		public void incLineRRefused(long lineRRefused) {
			this.lineRRefused += lineRRefused;
		}

		public long getLineTRefused() {
			return lineTRefused;
		}

		public void incLineTRefused(long lineTRefused) {
			this.lineTRefused += lineTRefused;
		}

		public long getPeriodInSeconds() {
			return periodInSeconds;
		}

		public void setPeriodInSeconds(long periodInSeconds) {
			this.periodInSeconds = periodInSeconds;
		}

		public long getLineRxTotal() {
			return lineRxTotal;
		}

		public void incLineRxTotal(long lineRxTotal) {
			this.lineRxTotal += lineRxTotal;
		}

		public long getLineTxTotal() {
			return lineTxTotal;
		}

		public void incLineTxTotal(long lineTxTotal) {
			this.lineTxTotal += lineTxTotal;
		}

		public long getByteRxTotal() {
			return byteRxTotal;
		}

		public void incByteRxTotal(long byteRxTotal) {
			this.byteRxTotal += byteRxTotal;
		}

		public long getByteTxTotal() {
			return byteTxTotal;
		}

		public void incByteTxTotal(long byteTxTotal) {
			this.byteTxTotal += byteTxTotal;
		}

		public String getSpeed(long byteNum, long seconds) {
			if (seconds == 0) {
				seconds = 1;
			}
			long bytePerSecond = byteNum / seconds;
			long unit = bytePerSecond;
			if ((unit = bytePerSecond / 1000000) > 0) {
				return unit + "MB/s";
			} else if ((unit = bytePerSecond / 1000) > 0) {
				return unit + "KB/s";
			} else{
				if (byteNum > 0 && bytePerSecond <= 0) {
					bytePerSecond = 1;
				}
				return bytePerSecond + "B/s";
			}
		}

		/**
		 * 获取平均行速度
		 * @param	lines
		 * 			行总数
		 * @param	seconds
		 * 			需要时间
		 * @return
		 */
		public String getLineSpeed(long lines, long seconds) {
			if (seconds == 0) {
				seconds = 1;
			}
			long linePerSecond = lines / seconds;
			
			if (lines > 0 && linePerSecond <= 0) {
				linePerSecond = 1;
			}
			
			return linePerSecond + "L/s";
		}

		/**
		 * 获取周期性的缓冲区状态
		 * @return
		 */
		public String getPeriodState() {
			return String.format("统计:  %s 速度 %s %s|", this.storage.info(),
					getSpeed(this.byteRx, this.periodInSeconds),
					getLineSpeed(this.lineRx, this.periodInSeconds));
		}

		public String getTotalStat() {
			String lineCount = this.storage.info();
			String[] lineCounts = this.storage.info().split(":");

			long lineRx = Long.parseLong(lineCounts[0]);
			long lineTx = Long.parseLong(lineCounts[1]);
			endTime = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long timeElapsed = (endTime.getTime() - beginTime.getTime()) / 1000;
			// statistic information for datax task
			Reporter.stat
					.put("READ_RECORDS", String.valueOf(lineRx).trim());
			Reporter.stat.put("WRITE_RECORDS", String.valueOf(lineTx)
					.trim());
			Reporter.stat.put("BEGIN_TIME", df.format(beginTime).trim());
			Reporter.stat.put("END_TIME", df.format(endTime).trim());
			Reporter.stat.put("BYTE_RX_TOTAL",
					String.valueOf(this.byteRxTotal).trim());
			
			return String.format("\n"
					+ "%-26s: %-18s\n" 
					+ "%-26s: %-18s\n"
					+ "%-26s: %19s\n"
					+ "%-26s: %19s\n" 
					+ "%-26s: %19s\n"
					+ "%-26s: %19s\n" ,
					 "开始工作时间", df.format(beginTime),
					 "结束工作时间", df.format(endTime),
					 "总共需要时间",  String.valueOf(timeElapsed) + "s",
					 "平均同步字节的速度", getSpeed(this.byteRxTotal, timeElapsed),
					 "平均插入行的速度", getLineSpeed(lineRx, timeElapsed),
					 "总共同步记录数", String.valueOf(lineRx));
		}
	}
}
