package com.gci.datax.engine.schedule;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.gci.datax.common.constants.ExitStatus;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.Pluginable;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.engine.conf.PluginConf;

/**
 * {@link ReaderWorker}代表{@link Reader}的执行类 </br>
 * 
 * */
public class ReaderWorker extends PluginWorker implements Runnable {
	private LineSender sender;

	private Method connect;

	private Method startRead;

	private Method finish;

	private static int globalIndex = 0;

	private static final Logger logger = Logger.getLogger(ReaderWorker.class);
	
	public ReaderWorker(PluginConf pluginConf, Class<?> myClass) {
		super(pluginConf, myClass);
		try {
			connect = myClass.getMethod("connect", new Class[] {});
			startRead = myClass
					.getMethod("startRead", new Class[] { Class
							.forName("com.gci.datax.common.plugin.LineSender") });
			finish = myClass.getMethod("finish", new Class[] {});
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
		this.setMyIndex(globalIndex++);
	}


	/**
	 * 读取数据
	 * 
	 * */
	@Override
	public void run() {
		try {
			int iRetcode = (Integer) init.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Reader初始化失败.");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) connect.invoke(myObject,
					new Object[] {});
			if (iRetcode != 0) {
				logger.error("Reader连接数据源失败 .");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) startRead.invoke(myObject,
					new Object[] { this.sender });
			if (iRetcode != 0) {
				logger.error("Reader开始读取数据失败.");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
			iRetcode = (Integer) finish.invoke(myObject, new Object[] {});
			if (iRetcode != 0) {
				logger.error("Reader完成加载数据失败.");
				//System.exit(ExitStatus.FAILED.value());
				return;
			}
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			//System.exit(ExitStatus.FAILED.value());
		}
	}
	
	public void setLineSender(LineSender sender) {
		this.sender = sender;
	}

}
