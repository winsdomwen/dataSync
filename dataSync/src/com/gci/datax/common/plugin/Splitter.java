package com.gci.datax.common.plugin;

import java.util.List;

import com.gci.datax.common.exception.DataExchangeException;


/**
 * 切分任务为多个子任务。
 * 
 * @see Reader
 * @see Writer
 * 
 * */
public abstract class Splitter extends AbstractPlugin {
	
	public abstract int init();
	
	public abstract List<PluginParam> split();
}
