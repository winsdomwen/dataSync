package com.gci.datax.plugins.writer.sqlserverwriter;

import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;
import com.gci.datax.plugins.writer.oraclejdbcwriter.ParamKey;

public class SqlServerWriterSplitter extends Splitter {
	private int concurrency = 1;

	@Override
	public int init() {
		// TODO Auto-generated method stub
		concurrency = param.getIntValue(ParamKey.concurrency, 1);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split() {
		// TODO Auto-generated method stub
		List<PluginParam> list = new ArrayList<PluginParam>();
		for (int i = 0; i < concurrency; i++) {
			PluginParam oParams = SplitUtils.copyParam(this.getParam());
			list.add(oParams);
		}
		return list;
	}

}
