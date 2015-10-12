package com.gci.datax.plugins.reader.oraclereader;

import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;
import com.gci.datax.plugins.common.DBSource;
import com.gci.datax.plugins.common.DBUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;


public class OracleReaderRowidSplitter extends Splitter {

	private Logger logger = Logger.getLogger(OracleReaderRowidSplitter.class);

	private String schema;

	private String tables;

	private String columns;

	private String where;

	private int rowidSplitMode = 3;

	private Connection connection;

	private String dbPoolKey;

	private static final String SQL_WITHOUT_WHERE_PATTERN = "select /*+ ROWID(a)*/ {0} from {1} a ";

	private static final String SQL_WITH_WHERE_PATTERN = "select /*+ ROWID(a)*/ {0} from {1} a where {2} ";

	private static final String SQL_ROWID_BETWEEN_PATTERN = " rowid between chartorowid(''{0}'') and chartorowid(''{1}'')";

	private static final String QUERY_PARTITIONS_PATTERN = "select SUBOBJECT_NAME from dba_objects where"
			+ " OWNER=upper(''{0}'') and OBJECT_NAME=upper(''{1}'')";

	private static String ORACLE_ROWID_SPLIT_PATTERN = "select "
			+ "dbms_rowid.rowid_create(1, B.data_object_id, A.relative_fno, A.min_block, 0) min_rowid, "
			+ "dbms_rowid.rowid_create(1, B.data_object_id, A.relative_fno, A.max_block+blocks-1, 10000) max_rowid "
			+ "from ( select  relative_fno,  block_id, "
			+ "min(block_id) over (partition by relative_fno) min_block, "
			+ "max(block_id) over (partition by relative_fno) max_block, "
			+ "blocks,  sum(blocks) over (partition by relative_fno) sum_blocks "
			+ "from ( select  relative_fno,  block_id,  blocks  "
			+ "from sys.dba_extents  where segment_name = ''{1}'' "
			+ "and owner = ''{0}'' order by block_id ) ) A,"
			+ "(select data_object_id from sys.DBA_objects "
			+ "where owner = ''{0}'' and object_name = ''{1}'') B "
			+ "where A.block_id = A.max_block and B.data_object_id IS NOT NULL";

	private static final String ORACLE_ROWID_NTILE_SPLIT_PATTERN = "select "
			+ "min(chartorowid(min_rowid)) min_rowid, "
			+ "max(chartorowid(max_rowid)) max_rowid "
			+ "from (select T.min_rowid, "
			+ "T.max_rowid, "
			+ "T.Relative_Fno, "
			+ "NTILE(200) over(order by T.Relative_Fno, chartorowid(T.min_rowid)) group_id "
			+ "from (select dbms_rowid.rowid_create(1, "
			+ "B.data_object_id, "
			+ "A.relative_fno, "
			+ "A.block_id, "
			+ "0) min_rowid, "
			+ "dbms_rowid.rowid_create(1, "
			+ "B.data_object_id, "
			+ "A.relative_fno, "
			+ "A.block_id + A.blocks - 1, "
			+ "10000) max_rowid, "
			+ "A.Relative_Fno "
			+ "from (select relative_fno, block_id, blocks "
			+ "from sys.dba_extents "
			+ "where segment_name = ''{1}'' "
			+ "and owner = ''{0}'' "
			+ "order by block_id) A, "
			+ "(select data_object_id "
			+ "from sys.DBA_objects where owner = ''{0}'' and object_name = ''{1}'') B "
			+ "where B.data_object_id IS NOT NULL) T) " + "group by group_id";

	private static final String ORACLE_ROWID_PARTITION_SPLIT_PATTERN = "select dbms_rowid.rowid_create(1, "
			+ "B.data_object_id, "
			+ "A.relative_fno, "
			+ "A.min_block, "
			+ "0) min_rowid, "
			+ "dbms_rowid.rowid_create(1, "
			+ "B.data_object_id, "
			+ "A.relative_fno, "
			+ "A.max_block + blocks - 1, "
			+ "10000) max_rowid "
			+ "from (select relative_fno, "
			+ "block_id, "
			+ "min(block_id) over(partition by relative_fno) min_block, "
			+ "max(block_id) over(partition by relative_fno) max_block, "
			+ "blocks, "
			+ "sum(blocks) over(partition by relative_fno) sum_blocks "
			+ "from (select relative_fno, block_id, blocks "
			+ "from sys.dba_extents "
			+ "where segment_name = ''{1}'' "
			+ "and owner = ''{0}'' "
			+ "and PARTITION_NAME= ''{2}'' "
			+ "order by block_id)) A, "
			+ "(select data_object_id "
			+ "from sys.DBA_objects "
			+ "where owner = ''{0}'' "
			+ "and object_name = ''{1}'' "
			+ "and SUBOBJECT_NAME = ''{2}'') B "
			+ "where A.block_id = A.max_block "
			+ "and B.data_object_id IS NOT NULL";

	enum RowidKey {
		tableName(0), tableMinRowid(1), tableMaxRowid(2);

		private int value;

		RowidKey(int value) {
			this.value = value;
		}
	}

	public OracleReaderRowidSplitter(PluginParam param) {
		setParam(param);
	}

	@Override
	public int init() {
		this.tables = this.param.getValue(ParamKey.tables);
		this.columns = this.param.getValue(ParamKey.columns, "*");
		this.where = this.param.getValue(ParamKey.where, "");
		this.schema = this.param.getValue(ParamKey.schema);
		this.rowidSplitMode = this.param.getIntValue(
				ParamKey.splitMod, this.rowidSplitMode);

		this.dbPoolKey = this.param
				.getValue(OracleReader.ORACLE_READER_DB_POOL_KEY);
		this.connection = DBSource.getConnection(this.dbPoolKey);

		return 0;
	}

	@Override
	public List<PluginParam> split() {
		List<PluginParam> params = new ArrayList<PluginParam>();
		String sql;

		/*table split */
		List<String> tablets = SplitUtils.splitTables(this.tables);

		List<Map<RowidKey, String>> rowids = new ArrayList<Map<RowidKey, String>>();

		for (String tablet : tablets) {
			List<String> partitions = this.analyzePartitions(tablet);
			this.logger.info(String.format("OracleReader 分析 %d 个切分 .", partitions.size()));
			if (null == partitions || 0 == partitions.size()) {
				if (1 == this.rowidSplitMode) {
					sql = format(ORACLE_ROWID_SPLIT_PATTERN,
							schema.toUpperCase(), tablet.toUpperCase());
				} else {
					sql = format(ORACLE_ROWID_NTILE_SPLIT_PATTERN,
							schema.toUpperCase(), tablet.toUpperCase());
				}
				rowids.addAll(this.analyzeRowids(tablet, sql));
			} else {
				for (String partition : partitions) {
					sql = format(ORACLE_ROWID_PARTITION_SPLIT_PATTERN,
							schema.toUpperCase(), tablet.toUpperCase(),
							partition.toUpperCase());
					rowids.addAll(this.analyzeRowids(tablet, sql));
				}
			}
		}

		if (0 == rowids.size()) {
			for (String tablet : tablets) {
				PluginParam iParam = SplitUtils.copyParam(this.param);
				sql = this.genNoRowidSql(tablet);
				logger.info(sql);
				iParam.putValue(ParamKey.sql, sql);
				params.add(iParam);
			}
			return params;
		}

		for (Map<RowidKey, String> perRowid : rowids) {
			PluginParam param = SplitUtils.copyParam(this.param);
			if (null == perRowid.get(RowidKey.tableMinRowid)) {
				sql = this.genNoRowidSql(perRowid.get(RowidKey.tableName));
			} else {
				sql = this.genRowidSql(perRowid.get(RowidKey.tableName),
						perRowid.get(RowidKey.tableMinRowid),
						perRowid.get(RowidKey.tableMaxRowid));
			}
			//logger.info(sql);
			param.putValue(ParamKey.sql, sql);
			params.add(param);
		}

		return params;
	}

	private List<String> analyzePartitions(String table) {
		List<String> partitions = new ArrayList<String>();

		String partitionsSql = format(QUERY_PARTITIONS_PATTERN,
				this.schema.toUpperCase(), table.toUpperCase());
		this.logger.info(String.format("分析切分查询: %s .",
				partitionsSql));

		ResultSet rs = null;
		try {
			rs = DBUtils.query(this.connection, partitionsSql);
			while (rs.next()) {
				String partition = rs.getString(1);
				if (StringUtils.isBlank(partition)) {
					continue;
				}
				partitions.add(partition);
			}
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			partitions.clear();
		} finally {
			DBUtils.closeResultSet(rs);
		}
		return partitions;
	}

	private List<Map<RowidKey, String>> analyzeRowids(String tablet, String sql) {
		List<Map<RowidKey, String>> rowids = new ArrayList<Map<RowidKey, String>>();

		ResultSet rs = null;
		try {
			this.logger.info(String.format("分析 Rowid 查询: %s .", sql));
			rs = DBUtils.query(this.connection, sql);
			if (!rs.next()) {
				logger.info(String.format("Table %s rowid 为空, 用none-rowid切分机制 .", tablet));
				Map<RowidKey, String> perRowid = new HashMap<RowidKey, String>();
				perRowid.put(RowidKey.tableName, tablet);
				rowids.add(perRowid);
			} else {
				do {
					String minRowid = rs.getString(1);
					String maxRowid = rs.getString(2);
					if (StringUtils.isBlank(minRowid)
							|| StringUtils.isBlank(maxRowid)) {
						continue;
					}
					Map<RowidKey, String> perRowid = new HashMap<RowidKey, String>();
					perRowid.put(RowidKey.tableName, tablet);
					perRowid.put(RowidKey.tableMinRowid, minRowid);
					perRowid.put(RowidKey.tableMaxRowid, maxRowid);
					rowids.add(perRowid);
				} while (rs.next());
			}
			return rowids;
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new IllegalStateException(e.getCause());
		} finally {
			DBUtils.closeResultSet(rs);
		}
	}

	private String genNoRowidSql(String table) {
		String sql;

		if (StringUtils.isBlank(this.where)) {
			sql = format(SQL_WITHOUT_WHERE_PATTERN, this.columns, this.schema
					+ "." + table);
		} else {
			sql = format(SQL_WITH_WHERE_PATTERN, this.columns, this.schema
					+ "." + table, this.where);
		}
		return sql;
	}

	private String genRowidSql(String table, String minRowid, String maxRowid) {
		String sql = "";
		String tail = format(SQL_ROWID_BETWEEN_PATTERN, minRowid, maxRowid);
		if (StringUtils.isBlank(this.where)) {
			sql = format(SQL_WITHOUT_WHERE_PATTERN, this.columns, this.schema
					+ "." + table)
					+ " where " + tail;
		} else {
			sql = format(SQL_WITH_WHERE_PATTERN, this.columns, this.schema
					+ "." + table, this.where)
					+ " and " + tail;
		}
		return sql;
	}

}
