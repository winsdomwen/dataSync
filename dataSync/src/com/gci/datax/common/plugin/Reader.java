package com.gci.datax.common.plugin;

import com.gci.datax.common.exception.DataExchangeException;

/**
 * {@link Reader} 代表插件{@link Pluginable}从数据源读取数据.
 * 
 * */
public abstract class Reader extends AbstractPlugin {
	
	public abstract int init() ;
	
	/**
	 * 连接数据源
	 * 
	 * @return
	 * 
	 * @throws	               {@link DataExchangeException}
	 *
	 * */
	public abstract int connect();
	
	/**
	 * 开始加载数据
	 * 
	 * @param	resultWriter
	 * 
	 * @return
	 * 
	 * @throws	{@link	DataExchangeException}
	 * */
	public abstract int startRead(LineSender sender);
	
	
	/**
	 * 完成后所做的事情，比如：释放资源 
	 * 
	 * @return
	 * 
	 * @throws	{@link	DataExchangeException}
	 * */
	public abstract int finish();
}
