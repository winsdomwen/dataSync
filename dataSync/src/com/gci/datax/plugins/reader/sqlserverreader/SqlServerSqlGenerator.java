package com.gci.datax.plugins.reader.sqlserverreader;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.util.SplitUtils;


/**
 * 根据配置生成查询sql
 */
public class SqlServerSqlGenerator {
	private PluginParam iParam;
	/**
	 * {0}是limit $cnt选项，sqlserver里为top $cnt
	 * {1}是columns
	 * {2}是table
	 * {3}是自定义查询条件where,group by,sort by
	 */
	private String SQLPattern = "select {0} {1} from {2} {3}";
	
	public static SqlServerSqlGenerator instance(PluginParam param) {
		return new SqlServerSqlGenerator(param);
	}
	
	private SqlServerSqlGenerator(PluginParam iParam) {
		this.iParam = iParam;
	}

	public List<String> generate() {
		List<String> sqls = new ArrayList<String>();
		List<String> tables = SplitUtils.splitTables(iParam.getValue(ParamKey.tables));
		
		String tmpSql = this.assemble();
		for (String table : tables) {
			String sql = MessageFormat.format(tmpSql, table);
			sqls.add(sql);
		}
		
		return sqls;
	}
	
	private String assemble() {
		String limit = this.parseLimit();
		String columns = this.parseCols();
		String where = this.parseWhere();
		String sql = MessageFormat.format(SQLPattern, limit, columns, "{0}", where);
		return sql;
	}

	private String parseWhere() {
		if (iParam.hasValue(ParamKey.where)) {
			return "where " +  iParam.getValue(ParamKey.where);
		} else {
			return "";
		}
	}

	private String parseCols() {
		if (iParam.hasValue(ParamKey.columns)) {
			return  iParam.getValue(ParamKey.columns);
		} else {
			return "*";
		}
	}

	private String parseLimit() {
		return "";
	}
}
