package com.gci.datax.plugins.reader.oraclereader;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;



public class OracleReaderTableSplitter extends Splitter {
	private static final String SQL_WITHOUT_WHERE_PATTERN = "select {0} from {1} a";

	private static final String SQL_WITH_WHERE_PATTERN = "select {0} from {1} a where {2}";

	private String schema;
	
	private String where;
	
	private String columns;

	private List<String> tables;

	private Logger logger = Logger.getLogger(OracleReaderTableSplitter.class);
	
	public OracleReaderTableSplitter(PluginParam iParam){
		param = iParam;
	}
	
	@Override
	public int init() {
		logger.info("OracleReaderTableSpliter初始化开始 .");

		tables = SplitUtils.splitTables(param.getValue(ParamKey.tables));

		schema = param.getValue(ParamKey.schema);
		
		where = param.getValue(ParamKey.where, "");
		
		columns = param.getValue(ParamKey.columns, "*");

		return PluginStatus.SUCCESS.value();
	}

	private String makeSql(String table) {
		if (StringUtils.isBlank(where)) {
			return format(SQL_WITHOUT_WHERE_PATTERN, this.columns, this.schema
					+ "." + table);
		} else {
			return format(SQL_WITH_WHERE_PATTERN, this.columns, this.schema
					+ "." + table, where);
		}
	}

	@Override
	public List<PluginParam> split() {
		List<PluginParam> params = new ArrayList<PluginParam>();

		for (String table : tables) {
			String sql = makeSql(table);
			PluginParam iParam = SplitUtils.copyParam(this.param);
			iParam.putValue(ParamKey.sql, sql);
			params.add(iParam);
		}

		return (params);
	}

}
