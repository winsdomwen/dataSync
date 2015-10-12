package com.gci.datax.common.plugin;

import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.MetaData;
import com.gci.datax.common.plugin.PluginMonitor;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Pluginable;

/**
 * 默认抽象类，实现{@link Pluginable}.
 * 
 * @see Pluginable
 * 
 * */
public class AbstractPlugin implements Pluginable {
	
	protected PluginParam param;

	protected PluginMonitor monitor;

	private String pluginName;

	private String pluginVersion;
	
	private MetaData myMetaData;

	private MetaData oppositeMetaData;

	@Override
	public PluginParam getParam() {
		return param;
	}

	@Override
	public void setParam(PluginParam param) {
		this.param = param;
	}


	@Override
	public String getPluginName() {
		return pluginName;
	}

	@Override
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	@Override
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}


	@Override
	public PluginMonitor getMonitor() {
		return monitor;
	}


	@Override
	public void setMonitor(PluginMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * 
	 * 
	 * @return
	 *			0:OK, 其它：failed .
	 *
	 * */
	@Override
	public int cleanup() {
		return 0;
	}

	@Override
	public int prepare(PluginParam param) {
		return 0;
	}

	@Override
	public int post(PluginParam param) {
		return 0;
	}

	@Override
	public List<PluginParam> split(PluginParam param) {
		List<PluginParam> paramList = new ArrayList<PluginParam>();
		paramList.add(param);
		return paramList;
	}
	
	@Override
	public MetaData getMyMetaData() {
		return this.myMetaData;
	}

	@Override
	public void setMyMetaData(MetaData md) {
		this.myMetaData = md;
	}

	@Override
	public MetaData getOppositeMetaData() {
		return this.oppositeMetaData;
	}

	@Override
	public void setOppositeMetaData(MetaData md) {
		this.oppositeMetaData = md;
	}

}
