package com.gci.datax.engine.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gci.datax.common.plugin.MetaData;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.common.util.StrUtils;

/**
 * 默认插件参数类{@link PluginParam}.
 * 
 * */
public class DefaultPluginParam implements PluginParam {
	private static final Logger LOG = Logger
			.getLogger(DefaultPluginParam.class);

	private final String KEY_NOT_EXISTS = "任务配置选项 [ %s ] 不存在.";

	private Map<String, String> params;

	private MetaData myMd;

	private MetaData oppositeMd;

	public DefaultPluginParam(Map<String, String> params) {
		this.params = params;
	}


	public boolean hasValue(String key) {
		if (null == key) {
			return false;
		}
		key = key.toLowerCase();
		if (!params.containsKey(key)) {
			return false;
		}
		String value = params.get(key);
		if (StringUtils.isBlank(value)) {
			return false;
		}
		return true;
	}

	@Override
	public void putValue(String key, String value) {
		this.params.put(key.toLowerCase(), value);
	}

	@Override
	public String getValue(String key) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (hasValue(key)) {
			return StrUtils.replaceString(this.params.get(key));
		}
		throw new IllegalArgumentException(String.format(KEY_NOT_EXISTS, key));
	}

	@Override
	public String getValue(String key, String defaultValue) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			return defaultValue;
		}
		return StrUtils.replaceString(this.params.get(key));
	}

	@Override
	public char getCharValue(String key) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			throw new IllegalArgumentException(String.format(KEY_NOT_EXISTS,
					key));
		}
		return StrUtils
				.changeChar(StrUtils.replaceString(this.params.get(key)));
	}

	@Override
	public char getCharValue(String key, char defaultValue) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			return defaultValue;
		}
		return getCharValue(key);
	}

	@Override
	public boolean getBoolValue(String key) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			throw new IllegalArgumentException(String.format(KEY_NOT_EXISTS,
					key));
		}
		return Boolean.valueOf(StrUtils.replaceString(this.params.get(key)));
	}

	@Override
	public boolean getBoolValue(String key, boolean defaultValue) {
		if (null != key)
			key = key.toLowerCase().trim();

		if (!hasValue(key))
			return defaultValue;

		return getBoolValue(key);
	}

	@Override
	public int getIntValue(String key) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			throw new IllegalArgumentException(String.format(KEY_NOT_EXISTS,
					key));
		}
		return Integer.valueOf(StrUtils.replaceString(this.params.get(key)));
	}

	@Override
	public int getIntValue(String key, int defaultValue) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			return defaultValue;
		}
		return getIntValue(key);
	}

	@Override
	public int getIntValue(String key, int defaultValue, int min, int max) {
		int value = getIntValue(key, defaultValue);
		
		if (value < min || value > max) {
			throw new IllegalArgumentException(String.format(
					"%s 的值为 %d, 必须在区间[%d, %d]内", key, value, min,
					max));
		}
		
		return value;
	}
	
	@Override
	public double getDoubleValue(String key) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			throw new IllegalArgumentException(String.format(KEY_NOT_EXISTS,
					key));
		}
		return Double.valueOf(StrUtils.replaceString(this.params.get(key)));
	}

	@Override
	public double getDoubleValue(String key, double defaultValue) {
		if (null != key) {
			key = key.toLowerCase().trim();
		}
		if (!hasValue(key)) {
			return defaultValue;
		}
		return getDoubleValue(key);
	}

	@Override
	public List<String> getAllKeys() {
		Iterator<String> it = params.keySet().iterator();
		ArrayList<String> listKey = new ArrayList<String>();
		while (it.hasNext()) {
			String key = it.next();
			listKey.add(key);
		}
		return listKey;
	}

	public void mergeTo(PluginParam param) {
		List<String> keys = this.getAllKeys();
		for (String k : keys) {
			if (!param.hasKey(k))
				param.putValue(k, this.getValue(k));
		}
	}

	@Override
	public void mergeTo(List<PluginParam> list) {
		for (PluginParam p : list) {
			mergeTo(p);
		}
	}

	@Override
	public boolean hasKey(String key) {
		return this.params.containsKey(key);
	}

	@Override
	public String toString() {
		String s = "";
		for (String key : this.params.keySet()) {
			String value = this.getValue(key, "");
			if (key.equalsIgnoreCase("password")) {
				value = value.replaceAll(".", "*");
			}
			s += String.format("\n\t%25s=[%-30s]", key, value);
		}
		return s;
	}

	@Override
	public MetaData getMyMetaData() {
		return this.myMd;
	}

	@Override
	public void setMyMetaData(MetaData md) {
		this.myMd = md;
	}

	@Override
	public MetaData getOppositeMetaData() {
		return this.oppositeMd;
	}

	@Override
	public void setOppositeMetaData(MetaData md) {
		this.oppositeMd = md;
	}

	@Override
	public PluginParam clone() {
		List<String> keyList = this.getAllKeys();
		PluginParam oParam = new DefaultPluginParam(
				new HashMap<String, String>());
		for (String key : keyList) {
			if (hasValue(key))
				oParam.putValue(key, this.getValue(key));
		}
		return oParam;
	}
}
