package com.gci.datax.engine.schedule;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.gci.datax.common.constants.ExitStatus;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.conf.PluginConf;

/**
 * {@link Writer}的具体执行类
 * 
 * @see ReaderWorker
 * 
 * */
public class WriterWorker extends PluginWorker implements Runnable {
	private LineReceiver receiver;

	private Method init;

	private Method connect;

	private Method startWrite;

	private Method commit;

	private Method finish;

	private static int globalIndex = 0;

	private static final Logger logger = Logger.getLogger(WriterWorker.class);

	public WriterWorker(PluginConf pluginConf, Class<?> myClass)  {
		super(pluginConf, myClass);
		try {
			init = myClass.getMethod("init", new Class[] {});
			connect = myClass.getMethod("connect", new Class[] {});
			startWrite = myClass
					.getMethod("startWrite", new Class[] { Class
							.forName("com.gci.datax.common.plugin.LineReceiver") });
			commit = myClass.getMethod("commit", new Class[] {});
			finish = myClass.getMethod("finish", new Class[] {});
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
		this.setMyIndex(globalIndex++);
	}

	public void setLineReceiver(LineReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * 写入数据
	 * 
	 * */
	@Override
	public void run() {
		try {
			int iRetcode = (Integer) init.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Writer初始化失败.");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) connect.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Writer连接数据源失败.");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) startWrite.invoke(myObject,
					new Object[] { receiver });
			if (iRetcode != 0) {
				logger.error("Writer开始写入数据失败 .");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) commit.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Writer提交事务失败 .");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) finish.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Writer完成任务失败 .");
				System.exit(ExitStatus.FAILED.value());
				return;
			}
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			//System.exit(ExitStatus.FAILED.value());
		}
	}

}
