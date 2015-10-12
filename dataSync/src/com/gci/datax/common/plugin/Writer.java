package com.gci.datax.common.plugin;

import com.gci.datax.common.exception.DataExchangeException;

/**
 * 代表插件{@link Pluginable}转储数据到目标数据源
 * 
 * @see {@link Pluginable}
 * @see {@link Reader}
 * 
 * */
public abstract class Writer extends AbstractPlugin {
	
	public abstract int init();
	
	public abstract int connect();

	public abstract int startWrite(LineReceiver receiver);
	
	public abstract int commit();
	
	public abstract int finish();
}
