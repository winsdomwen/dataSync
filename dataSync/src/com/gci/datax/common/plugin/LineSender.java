package com.gci.datax.common.plugin;

/**
 * {@link Reader} 和 {@link Writer}通过{@link Storage}去交换数据,
 * {@link Reader} 用{@link LineSender}在{@link Storage}中获取数据.
 * 
 *  @see LineReceiver
 *  @see BufferedLineExchanger
 *  
 * */
public interface LineSender {
	
	public Line createLine();
	
	/**
	 * 发送一行{@link Line} 到 {@link Storage}.
	 * 
	 * @param line			
	 * 
	 * @return		
	 *
	 * */
	public boolean sendToWriter(Line line);
	
	/**
	 * test
	 * */
	public boolean fakeSendToWriter(int lineLength);
	
	/**
	 * 清空缓冲区的数据(如果存在的话){@link Storage}.
	 * 
	 * */
	public void flush();
}
