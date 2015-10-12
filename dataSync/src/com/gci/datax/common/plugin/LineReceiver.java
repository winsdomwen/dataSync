package com.gci.datax.common.plugin;

/**
 * {@link Reader} 和 {@link Writer}通过{@link Storage}去交换数据,
 * {@link Writer}用{@link LineReceiver}在{@link Storage}中获取数据(通常在内存中).
 *  
 * @see LineSender
 * @see BufferedLineExchanger
 * 
 * */
public interface LineReceiver {
	
	/**
	 * 取下一个{@link Line}
	 * 
	 * @return	{@link Line}
	 * 			
	 * 
	 * */
	public Line getFromReader();
}
