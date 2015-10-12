package com.gci.datax.engine.schedule;

import java.lang.reflect.Method;
import java.util.List;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.engine.conf.PluginConf;


/**
 * {@link Splitter}的具体执行类
 * 
 * */
public class SplitWorker extends PluginWorker {
	private Method init;

	private Method split;

	public SplitWorker(PluginConf pluginConf, Class<?> myClass) {
		super(pluginConf, myClass);
		try {
			init = myClass.getMethod("init", new Class[] {});
			split = myClass.getMethod("split", new Class[] {});
		}  catch (Exception e) {
			throw new DataExchangeException(e.getCause());
		}
	}

	/**
	 * 切分任务为多个子任务
	 *
	 * @return
	 * 
	 * @throws	{@link DataExchangeException}
	 *  
	 * */
	@SuppressWarnings("unchecked")
	public List<PluginParam> doSplit() {
		try {
			int iRetCode = (Integer) init.invoke(myObject, new Object[] {});
			if (iRetCode != 0)
				throw new DataExchangeException("切分器切分任务失败 .");
			return (List<PluginParam>) split.invoke(myObject, new Object[] {});
		} catch (Exception e) {
			throw new DataExchangeException(e.getCause());
		}
	}
}
