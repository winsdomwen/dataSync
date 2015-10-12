package com.gci.datax.engine.conf;

import com.gci.datax.engine.schedule.Engine;

/**
 * Engine的配置,包括Engine的核心信息
 * 
 * @see	{@link Engine}
 * 
 */
public class EngineConf {
	private String storageClassName;

	private int version;

	private int storageLineLimit;

	private int storageByteLimit;

	private int storageBufferSize;

	private String pluginRootPath;
	
	static private EngineConf instance;
	
	private EngineConf(){
	}
	
	/**
	 * 单例
	 *
        * @return
        */
	static public EngineConf getInstance() {
		if (null == instance) {
			instance = new EngineConf();
		}
		return instance;
	}
	

	public String getStorageClassName() {
		return storageClassName;
	}

	public void setStorageClassName(String storageClassName) {
		this.storageClassName = storageClassName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


	public int getStorageLineLimit() {
		return storageLineLimit;
	}

	public void setStorageLineLimit(int storageLineLimit) {
		this.storageLineLimit = storageLineLimit;
	}
 
	public int getStorageByteLimit() {
		return storageByteLimit;
	}

	public void setStorageByteLimit(int storageByteLimit) {
		this.storageByteLimit = storageByteLimit;
	}

	public String getPluginRootPath() {
		return pluginRootPath;
	}

	public void setPluginRootPath(String pluginRootPath) {
		this.pluginRootPath = pluginRootPath;
	}

	public int getStorageBufferSize() {
		return storageBufferSize;
	}

	public void setStorageBufferSize(int storageBufferSize) {
		this.storageBufferSize = storageBufferSize;
	}

	public String toString() {
         return String
                 .format("数据交换引擎[version=%d] 存储[%s size %d/%d buffer %d] 插件路径[%s]",
                         this.getVersion(), this.getStorageClassName(),
                         this.getStorageLineLimit(), this.getStorageByteLimit(),
                         this.getStorageBufferSize(), this.getPluginRootPath());
	}

}
