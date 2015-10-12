package com.gci.datax.plugins.writer.oraclewriter;

import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;


public class OracleWriterSplitter extends Splitter {
	private int concurrency = 1;

	@Override
	public int init() {
		concurrency = param.getIntValue(ParamKey.concurrency, 1);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split() {
		List<PluginParam> v = new ArrayList<PluginParam>();
		for (int i = 0; i < concurrency; i++) {
			PluginParam oParams = SplitUtils.copyParam(this.getParam());
			v.add(oParams);
		}
		return v;
	}

}
