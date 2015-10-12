package com.gci.datax.engine.conf;

import com.gci.datax.common.plugin.PluginParam;

/**
 * 描述插件的配置，used by job
 * 
 * @see JobConf
 * 
 * */
public class JobPluginConf {	
	private String id;

	private String name;

	private PluginParam pluginParams;

	private int destructLimit = 0; 

	private static final int THREAD_MIN = 0;
	
	private static final int THREAD_MAX = 64;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConcurrency() {
		return this.pluginParams.getIntValue("concurrency");
	}

	public void setConcurrency(int concurrency) {
		this.pluginParams.putValue("concurrency", String.valueOf(concurrency));
	}

	public PluginParam getPluginParams() {
		return pluginParams;
	}

	public void setPluginParams(PluginParam plugParams) {
		this.pluginParams = plugParams;
	}

	public int getDestructLimit() {
		return destructLimit;
	}

	public void setDestructLimit(int destructLimit) {
		this.destructLimit = destructLimit;
	}

	boolean validate() {
		int concurrency = this.getConcurrency();
        return !(concurrency < THREAD_MIN || concurrency > THREAD_MAX);
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		sb.append(String.format("\nname:%s id %s, pool(%d) destruct(%d)",
				this.getName(), this.getId(), 
				this.getConcurrency(), this.getDestructLimit()));
		sb.append(String.format("\n参数:%s", this.getPluginParams()
				.toString()));
		return sb.toString();
	}
}
