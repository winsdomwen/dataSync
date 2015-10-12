package com.gci.datax.common.plugin;

import java.util.List;

/**
 * 为插件{@link Pluginable}所定义的一些key-value参数,定义在job xml文件中 。
 * 
 * */
public interface PluginParam extends Cloneable {

	public String getValue(String sKey);

	public String getValue(String key, String defaultValue);

	public boolean getBoolValue(String key);

	public boolean getBoolValue(String key, boolean defaultValue);

	public char getCharValue(String key);

	public char getCharValue(String key, char defaultValue);

	public int getIntValue(String key);

	public int getIntValue(String key, int defaultValue);

	public int getIntValue(String key, int defaultValue,
			int min, int max);
	
	public double getDoubleValue(String key);

	public double getDoubleValue(String key, double defaultValue);

	public void putValue(String key, String value);

	public boolean hasKey(String key);

	public boolean hasValue(String key);

	public List<String> getAllKeys();

	public void mergeTo(PluginParam param);

	public void mergeTo(List<PluginParam> param);

	public String toString();

	public MetaData getMyMetaData();

	public void setMyMetaData(MetaData md);

	public MetaData getOppositeMetaData();

	public void setOppositeMetaData(MetaData md);

	public PluginParam clone();
}
