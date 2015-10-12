package com.gci.datax.engine.schedule;

import com.gci.datax.common.constants.Constants;
import com.gci.datax.common.constants.ExitStatus;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Pluginable;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.conf.*;
import com.gci.datax.engine.plugin.BufferedLineExchanger;
import com.gci.datax.engine.storage.Storage;
import com.gci.datax.engine.storage.StoragePool;
import com.gci.datax.engine.tools.JobConfGenDriver;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 核心类，数据交换的入口
 * 
 * */
public class Engine {
	private static final Logger logger = Logger.getLogger(Engine.class);

	private static final int PERIOD = 10; //10秒钟统计报告一次

	private static final int MAX_CONCURRENCY = 64; //最大并发数

	private EngineConf engineConf;

	private Map<String, PluginConf> pluginReg;

	private MonitorPool readerMonitorPool;

	private MonitorPool writerMonitorPool;

	/**
	 * 构造函数
	 * 
	 * @param engineConf
	 *            {@link Engine}配置
	 * 
	 * @param pluginReg
	 *            {@link Pluginable}配置
	 * 
	 * */
	public Engine(EngineConf engineConf, Map<String, PluginConf> pluginReg) {
		this.engineConf = engineConf;
		this.pluginReg = pluginReg;

		this.writerMonitorPool = new MonitorPool();
		this.readerMonitorPool = new MonitorPool();

	}

	/**
	 * 开始执行任务
	 *
	 * @param jobConf
	 * 
	 * @return 0:success, 其它:failure.
	 *
	 * @throws Exception
       *
       */

 	public int start(JobConf jobConf) throws Exception {
		logger.info('\n' + engineConf.toString() + '\n');
		logger.info('\n' + jobConf.toString() + '\n');
		logger.info("开始工作 .");

		StoragePool storagePool = new StoragePool(jobConf, engineConf, PERIOD);
		NamedThreadPoolExecutor readerPool = initReaderPool(jobConf,
				storagePool);
		List<NamedThreadPoolExecutor> writerPool = initWriterPool(jobConf,
				storagePool);

		logger.info("开始交换数据 .");
		readerPool.shutdown();
		for (NamedThreadPoolExecutor dp : writerPool) {
			dp.shutdown();
			//dp.shutdownNow();
		}
		
		int sleepCnt = 0;
		int retcode = 0;

		while (true) {
			//检查reader是否完成
			boolean readerFinish = readerPool.isTerminated();
			if (readerFinish) {
				storagePool.closeInput();
			}

			boolean writerAllFinish = true;

			NamedThreadPoolExecutor[] dps = writerPool
					.toArray(new NamedThreadPoolExecutor[0]);

			for (NamedThreadPoolExecutor dp : dps) {
				if (!readerFinish && dp.isTerminated()) {
					logger.error(String.format("Writer %s 失败 .",
							dp.getName()));
					writerPool.remove(dp);
				} else if (!dp.isTerminated()) {
					writerAllFinish = false;
				}
			}

			if (readerFinish && writerAllFinish) {
				logger.info("Reader开始提交数据 .");
				readerPool.doPost();
				logger.info("Reader结束提交数据 .");

				logger.info("Writers开始提交数据 .");
				for (NamedThreadPoolExecutor dp : writerPool) {
					dp.getParam().setOppositeMetaData(
							readerPool.getParam().getMyMetaData());
					//执行同步后的sql或存储过程
					dp.doPost();
					//关闭数据源
					dp.doCleanup();
				}
				logger.info("Writers结束提交数据 .");

				logger.info("同步数据成功 .");
				break;
			} else if (!readerFinish && writerAllFinish) {
				logger.error("Writers在reader完成前完成.");
				logger.error("同步数据失败.");
				readerPool.shutdownNow();
				readerPool.awaitTermination(3, TimeUnit.SECONDS);
				break;
			}
//			readerPool.purge();
//			for (NamedThreadPoolExecutor dp : writerPool) {
//				dp.purge();
//			}
//			logger.info("清理线程池");

			Thread.sleep(1000);
			sleepCnt++;

			//每10秒统计一次
			if (sleepCnt % PERIOD == 0) {
				/* reader&writer count num of thread */
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("ReaderPool %s: 活动线程数 %d .",
						readerPool.getName(), readerPool.getActiveCount()));
				logger.info(sb.toString());

				sb.setLength(0);
				for (NamedThreadPoolExecutor perWriterPool : writerPool) {
					sb.append(String.format(
							"WriterPool %s: 活动线程数 %d .",
							perWriterPool.getName(),
							perWriterPool.getActiveCount()));
					logger.info(sb.toString());
					sb.setLength(0);
				}
				logger.info(storagePool.getPeriodState());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(storagePool.getTotalStat());
		long discardLine = this.writerMonitorPool.getDiscardLine();
		sb.append(String.format("%-26s: %19d\n", "总共出错丢弃的记录数",
				discardLine));

		logger.info(sb.toString());

//		Reporter.stat.put("DISCARD_RECORDS", String.valueOf(discardLine));
//		Reporter reporter = Reporter.instance();
//		reporter.report(jobConf);

		long total = -1;
		boolean writePartlyFailed = false;
		for (Storage s : storagePool.getStorageForReader()) {
			String[] lineCounts = s.info().split(":");
			long lineTx = Long.parseLong(lineCounts[1]);
			if (total != -1 && total != lineTx) {
				writePartlyFailed = true;
				logger.error("Writer部分失败, 因 " + total + "!="
						+ lineTx);
			}
			total = lineTx;
		}
		return writePartlyFailed ? 200 : retcode;
	}

	/**
	 * 配置log4j环境.
	 * 
	 * @param jobId
	 * 
	 * */
	public static void confLog(String jobId) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		String logDir = Constants.DATAX_LOCATION + "/logs/" + f.format(c.getTime());
		System.setProperty("log.dir", logDir);
		f = new java.text.SimpleDateFormat("HHmmss");
		//String logFile = jobId + "." + f.format(c.getTime()) + ".log";
		String logFile = jobId + ".log";
		System.setProperty("log.file", logFile);
		PropertyConfigurator.configure(Constants.LOG4JPRO);
	}

	private NamedThreadPoolExecutor initReaderPool(JobConf jobConf,
			StoragePool sp) throws Exception {

		JobPluginConf readerJobConf = jobConf.getReaderConf();
		PluginConf readerConf = pluginReg.get(readerJobConf.getName());

		if (readerConf.getPath() == null) {
			readerConf.setPath(engineConf.getPluginRootPath() + "reader/"
					+ readerConf.getName());
		}

		logger.info(String.format("Reader %s 尝试加载路径 %s .",
				readerConf.getName(), readerConf.getPath()));
		JarLoader jarLoader = new JarLoader(
				new String[] { readerConf.getPath() });
		Class<?> myClass = jarLoader.loadClass(readerConf.getClassName());

		ReaderWorker readerWorkerForPreAndPost = new ReaderWorker(readerConf,
				myClass);
		PluginParam sparam = jobConf.getReaderConf().getPluginParams();

		readerWorkerForPreAndPost.setParam(sparam);
		readerWorkerForPreAndPost.init();

		logger.info("Reader准备工作开始 .");
		int code = readerWorkerForPreAndPost.prepare(sparam);
		if (code != 0) {
			throw new DataExchangeException("Reader准备工作失败!");
		}
		logger.info("Reader准备工作结束 .");

		logger.info("Reader分切工作开始 .");
		List<PluginParam> readerSplitParams = readerWorkerForPreAndPost
				.doSplit(sparam);
		logger.info(String.format(
				"Reader分切工作为%d 个子任务",
				readerSplitParams.size()));
		logger.info("Reader分切工作结束 .");

		int concurrency = readerJobConf.getConcurrency();
		if (concurrency <= 0 || concurrency > MAX_CONCURRENCY) {
			throw new IllegalArgumentException(String.format(
					"Reader并发线程设置为 %d, 确保它在区间内 [%d, %d] .",
					concurrency, 1, MAX_CONCURRENCY));
		}

		concurrency = Math.min(concurrency,
				readerSplitParams.size());
		if (concurrency <= 0) {
			concurrency = 1;
		}
		readerJobConf.setConcurrency(concurrency);

		NamedThreadPoolExecutor readerPool = new NamedThreadPoolExecutor(
				readerJobConf.getId(), readerJobConf.getConcurrency(),
				readerJobConf.getConcurrency(), 1L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		readerPool.setPostWorker(readerWorkerForPreAndPost);
		readerPool.setParam(sparam);

		readerPool.prestartAllCoreThreads();

		logger.info("Reader开始读取数据 .");
		for (PluginParam param : readerSplitParams) {
			ReaderWorker readerWorker = new ReaderWorker(readerConf, myClass);
			readerWorker.setParam(param);
			readerWorker.setLineSender(new BufferedLineExchanger(null, sp
					.getStorageForReader(), this.engineConf
					.getStorageBufferSize()));
			readerPool.execute(readerWorker);
			readerMonitorPool.monitor(readerWorker);
		}

		return readerPool;
	}

	private List<NamedThreadPoolExecutor> initWriterPool(JobConf jobConf,
			StoragePool sp) throws Exception {
		List<NamedThreadPoolExecutor> writerPoolList = new ArrayList<NamedThreadPoolExecutor>();
		List<JobPluginConf> writerJobConfs = jobConf.getWriterConfs();
		for (JobPluginConf dpjc : writerJobConfs) {
			PluginConf writerConf = pluginReg.get(dpjc.getName());
			if (writerConf.getPath() == null) {
				writerConf.setPath(engineConf.getPluginRootPath() + "writer/"
						+ writerConf.getName());
			}

			logger.info(String.format(
					"Writer %s 尝试加载路径 %s .",
					writerConf.getName(), writerConf.getPath()));
			JarLoader jarLoader = new JarLoader(
					new String[] { writerConf.getPath() });
			Class<?> myClass = jarLoader.loadClass(writerConf.getClassName());

			WriterWorker writerWorkerForPreAndPost = new WriterWorker(
					writerConf, myClass);

			PluginParam writerParam = dpjc.getPluginParams();
			writerWorkerForPreAndPost.setParam(writerParam);
			writerWorkerForPreAndPost.init();

			logger.info("Writer准备工作开始 .");
			//执行同步前的sql或存储过程
			int code = writerWorkerForPreAndPost.prepare(writerParam);
			if (code != 0) {
				throw new DataExchangeException(
						"Writer准备工作失败!");
			}
			logger.info("Writer准备工作结束 .");

			logger.info("Writer分切工作开始.");
			List<PluginParam> writerSplitParams = writerWorkerForPreAndPost
					.doSplit(writerParam);
			logger.info(String.format(
					"Writer分切工作为 %d 个子任务 .",
					writerSplitParams.size()));
			logger.info("Writer分切工作结束 .");

			int concurrency = dpjc.getConcurrency();
			if (concurrency <= 0 || concurrency > MAX_CONCURRENCY) {
				throw new IllegalArgumentException(String.format(
						"Writer并发线程设置为 %d, 确保它在区间内 [%d, %d] .",
						concurrency, 1, MAX_CONCURRENCY));
			}
	
			concurrency = Math.min(dpjc.getConcurrency(),
					writerSplitParams.size());
			if (concurrency <= 0) {
				concurrency = 1;
			}
			dpjc.setConcurrency(concurrency);

			NamedThreadPoolExecutor writerPool = new NamedThreadPoolExecutor(
					dpjc.getName() + "-" + dpjc.getId(), dpjc.getConcurrency(),
					dpjc.getConcurrency(), 1L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());

			writerPool.setPostWorker(writerWorkerForPreAndPost);
			writerPool.setParam(writerParam);

			writerPool.prestartAllCoreThreads();
			writerPoolList.add(writerPool);
			logger.info("Writer开始插入数据 .");

			for (PluginParam pp : writerSplitParams) {
				WriterWorker writerWorker = new WriterWorker(writerConf,
						myClass);
				writerWorker.setParam(pp);
				writerWorker.setLineReceiver(new BufferedLineExchanger(sp
						.getStorageForWriter(dpjc.getId()), null,
						this.engineConf.getStorageBufferSize()));
				writerPool.execute(writerWorker);
				writerMonitorPool.monitor(writerWorker);
			}
		}
		return writerPoolList;
	}

	public static void main(String[] args) throws Exception {
		String jobDescFile = null;
		if (args.length < 1) {
			System.exit(JobConfGenDriver.produceXmlConf());
		} else if (args.length == 1) {
			jobDescFile = args[0];
		} else {
			System.out.printf("Usage: java -jar engine.jar job.xml .");
			System.exit(ExitStatus.FAILED.value());
		}

		//confLog("BEFORE_CHRIST");
		JobConf jobConf = ParseXMLUtil.loadJobConfig(jobDescFile);
		confLog(jobConf.getId());
		EngineConf engineConf = ParseXMLUtil.loadEngineConfig();
		Map<String, PluginConf> pluginConfs = ParseXMLUtil.loadPluginConfig();

		Engine engine = new Engine(engineConf, pluginConfs);

		int retcode = 0;
		try {
			retcode = engine.start(jobConf);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			System.exit(ExitStatus.FAILED.value());
		}
		System.exit(retcode);
	}
	
	public static void dataChange(String jobName,String filePath){
		JobConf jobConf = ParseXMLUtil.loadJobConfig(filePath);
		confLog(jobName);
		EngineConf engineConf = ParseXMLUtil.loadEngineConfig();
		Map<String, PluginConf> pluginConfs = ParseXMLUtil.loadPluginConfig();

		Engine engine = new Engine(engineConf, pluginConfs);

		try {
			engine.start(jobConf);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
		}
	}

}
