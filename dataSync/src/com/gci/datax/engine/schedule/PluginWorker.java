package com.gci.datax.engine.schedule;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.PluginMonitor;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Pluginable;
import com.gci.datax.engine.conf.PluginConf;

/**
 * {@link PluginWorker} 代表{@link Pluginable}的执行类.</br>
 * 通过反射加载插件的方法
 * 
 * */
public abstract class PluginWorker {

	protected String pluginName;

	protected Method getParam;

	protected Method setParam;

	protected Method setMonitor;

	protected Method getMonitor;
	
	protected Method init;

	protected Method prepare;

	protected Method post;

	protected Method split;

	protected Method cleanup;

	protected Method regMyMetaData;

	protected Method getMyMetaData;

	protected Method setOppositeMetaData;

	protected Method getOppositeMetaData;

	protected Object myObject;

	protected static Class<?> myClass;

	protected int myIndex = 0;

	private Logger logger = Logger.getLogger(PluginWorker.class);

	public PluginWorker(PluginConf pluginConf, Class<?> myClass) {
		try {
			PluginWorker.myClass = myClass;
			this.pluginName = pluginConf.getName();
			this.myObject = myClass.newInstance();
			this.getParam = myClass.getMethod("getParam", new Class[] {});
			this.setParam = myClass.getMethod("setParam",
					new Class[] { Class.forName("com.gci.datax.common.plugin.PluginParam") });

			this.setMonitor = myClass
					.getMethod("setMonitor", new Class[] { Class
							.forName("com.gci.datax.common.plugin.PluginMonitor") });
			this.getMonitor = myClass.getMethod("getMonitor", new Class[] {});

			this.init = myClass.getMethod("init", 
					new Class[] {});
			
			this.split = myClass.getMethod("split",
					new Class[] { Class.forName("com.gci.datax.common.plugin.PluginParam") });
			
			this.prepare = myClass.getMethod("prepare",
					new Class[] { Class.forName("com.gci.datax.common.plugin.PluginParam") });

			this.post = myClass.getMethod("post",
					new Class[] { Class.forName("com.gci.datax.common.plugin.PluginParam") });

			this.cleanup = myClass.getMethod("cleanup", new Class[] {});

			this.regMyMetaData = myClass.getMethod("setMyMetaData",
					new Class[] { Class.forName("com.gci.datax.common.plugin.MetaData") });

			this.getMyMetaData = myClass.getMethod("getMyMetaData",
					new Class[] {});

			this.setOppositeMetaData = myClass.getMethod("setOppositeMetaData",
					new Class[] { Class.forName("com.gci.datax.common.plugin.MetaData") });

			this.getOppositeMetaData = myClass.getMethod("getOppositeMetaData",
					new Class[] {});

		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}

	}

	public int init() {
		try {
			return (Integer) init.invoke(myObject);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}
	
	public int prepare(PluginParam oParam) {
		try {
			return (Integer) prepare.invoke(myObject, new Object[]{oParam});
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public int post(PluginParam oParam) {
		try {
			return (Integer) post.invoke(myObject, new Object[] { oParam });
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}

	@SuppressWarnings("unchecked")
	public List<PluginParam> doSplit(PluginParam oParam) {
		try {
			return (List<PluginParam>) split.invoke(myObject,
					new Object[] { oParam });
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}

	public int cleanup() {
		try {
			return (Integer) cleanup.invoke(myObject, new Object[] {});
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public PluginParam getParam() {
		try {
			return (PluginParam) getParam.invoke(myObject, new Object[] {});
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public void setParam(PluginParam oParam) {
		try {
			setParam.invoke(myObject, new Object[] { oParam });
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public PluginMonitor getMonitor() {
		try {
			return (PluginMonitor) getMonitor.invoke(myObject, new Object[] {});
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public void setMonitor(PluginMonitor monitor)  {
		try {
            setMonitor.invoke(myObject, new Object[]{monitor});
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public int getMyIndex() {
		return myIndex;
	}

	/**
	 * 设置插件的index
	 * 
	 * @param	myIndex
	 * 
	 */
	public void setMyIndex(int myIndex) {
		this.myIndex = myIndex;
	}

}
