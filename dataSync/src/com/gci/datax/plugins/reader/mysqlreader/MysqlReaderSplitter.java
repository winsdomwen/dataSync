package com.gci.datax.plugins.reader.mysqlreader;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;


public class MysqlReaderSplitter extends Splitter {
	
	private List<String> tableNames;

	private static final String SQL_WITHOUT_WHERE_PATTERN = "select {0} from {1}";

	private static final String SQL_WITH_WHERE_PATTERN = "select {0} from {1} where {2}";

	private Logger logger = Logger.getLogger(MysqlReaderSplitter.class);
	
	public MysqlReaderSplitter(PluginParam iParam){
		param = iParam;
	}
	                                           
	
	@Override
	public int init() {
		String tables = param.getValue(ParamKey.tables);
		tableNames = SplitUtils.splitTables(tables);
		logger.info("MysqlReaderSpliter初始化成功.");
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split() {
		return this.splitParamsList(param);
	}

	private List<PluginParam> splitParamsList(PluginParam iParams) {
		ArrayList<PluginParam> listParms = new ArrayList<PluginParam>();
		
		String where = iParams.getValue(ParamKey.where, null);
		String columns = iParams.getValue(ParamKey.columns,
				"*");
		for (String table : tableNames) {
			PluginParam oParams = SplitUtils.copyParam(iParams);
			String sql;
			if (StringUtils.isBlank(where)) {
				sql = format(SQL_WITHOUT_WHERE_PATTERN, columns, table);
			} else {
				sql = format(SQL_WITH_WHERE_PATTERN, columns, table, where);
			}
			oParams.putValue(ParamKey.sql, sql);
			listParms.add(oParams);
		}
		logger.info(" MysqlReaderSpliter切分成功.");
		return listParms;
	}
}
