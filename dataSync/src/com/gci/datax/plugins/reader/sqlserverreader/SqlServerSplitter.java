package com.gci.datax.plugins.reader.sqlserverreader;

import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;


/**
 * 根据配置的tables数量分割查询
 */
public class SqlServerSplitter extends Splitter{
	
	public SqlServerSplitter(PluginParam iParam) {
		param = iParam;
	}

	public List<PluginParam> split() {
		List<PluginParam> paramList = new ArrayList<PluginParam>();
		
		List<String> sqls = SqlServerSqlGenerator.instance(param).generate();
		for (String sql : sqls) {
			PluginParam iParam = SplitUtils.copyParam(param);
			iParam.putValue(ParamKey.sql, sql);
			paramList.add(iParam);
		}
		return paramList;
	}

	@Override
	public int init() {
		return PluginStatus.SUCCESS.value();
	}
}
