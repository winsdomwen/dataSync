package com.gci.datax.common.plugin;

/**
 *  代表oracle、mysql等数据源的一条数据记录
 *  {@link Reader}和{@link Writer}通过Line进行数据交换
 *  (通常每次是交换多行记录).
 *  
 * */
public interface Line {
	
	/**
	 * 添加一个字段到{@link Line}.
	 * 
	 * @param	field	
	 * 			字段
	 * 
	 * @return 
	 * 			true or false
	 * 
	 * */
	public boolean addField(String field);
	
	/**
	 * 添加一个字段到{@link Line}.
	 * 
	 * @param	field	
	 * 			字段
	 * 
	 * @param 	index	
	 * 			位置
	 * 
	 * @return 
	 *			true or false
	 *
	 * */
	public boolean addField(String field, int index);
	
	public String getField(int idx);
	
	public String checkAndGetField(int idx);
	
	public int getFieldNum();
	
	public StringBuffer toStringBuffer(char separator);
	
	public String toString(char separator);
	
	public Line fromString(String lineStr, char separator);
	

	public int length();
	
}
