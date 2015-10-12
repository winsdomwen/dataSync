package com.gci.datax.common.plugin;

import java.util.List;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.plugins.writer.mysqlwriter.MysqlWriter;


/**
 * 具体插件必须实现的基础接口. 该接口表示一个plug_in
 * (它跑起来时，代表一个工作任务).<br> 
 * {@link Reader} 和 {@link Writer}都是插件<br>
 * 可以扩展{@link Reader}或者{@link Writer}去实现自己的{@link Pluginable}插件.
 * 
 * */
public interface Pluginable {

	public PluginParam getParam();
	
	public void setParam(PluginParam oParam);
	
	public PluginMonitor getMonitor();
	
	public void setMonitor(PluginMonitor monitor);
	
	public String getPluginName();
	
	public void setPluginName(String pluginName);
	
	public String getPluginVersion();
	
	public void setPluginVersion(String pluginVersion);	
	
	/**
	 * 切分任务为多个子任务
	 * 
	 * @param	param		
	 * 
	 * @return
	 * 
	 * @throws
	 * 			DataExchangeException, 
	 * 			
	 * */
	public List<PluginParam> split(PluginParam param);
	
	/**
	 * 数据交换前所做的准备，例如:可以先用sql删除目标数据源的数据
	 * 
	 * @param	param
	 * 
	 * @return
	 * 
	 * @throws
	 * 			DataExchangeException, 
	 * */
	public int prepare(PluginParam param);
	
	/**
	 * 数据交换后所做的事后处理，例如：交换数据后可以执行sql通知其它表，或执行存储过程之类的
	 * 
	 * @param	param	
	 * 
	 * @return
	 * 
	 * @throws
	 * 			DataExchangeException, 
	 * */
	public int post(PluginParam param);
	

	public int cleanup();
	
	public MetaData getMyMetaData();

	public void setMyMetaData(MetaData md);

	public MetaData getOppositeMetaData();

	public void setOppositeMetaData(MetaData md);
	
}
