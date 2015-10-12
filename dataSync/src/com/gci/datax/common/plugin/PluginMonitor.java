package com.gci.datax.common.plugin;

/**
 * 监控器监控{@link Pluginable}的状态:记录行数, 失败行数，等等.
 * 
 * @see {@link PluginStatus}
 * */
public interface PluginMonitor {
	
	public long getSuccessedLines();
	
	public long getFailedLines();

	public int setSuccessedLines(long num);

	public int setFailedLines(long num);

	public int lineSuccess();

	public int lineFail(String info);

	public void setStatus(PluginStatus status);
	
	public PluginStatus getStatus();
	
	public String getTargetName();
	
	public void setTargetName(String targetName);
	
	public int getTargetId();
	
	public void setTargetId(int targetId);
	
}
