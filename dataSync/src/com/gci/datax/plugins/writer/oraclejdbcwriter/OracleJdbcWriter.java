package com.gci.datax.plugins.writer.oraclejdbcwriter;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.plugins.common.DBSource;

/**
 * oracle jdbc writer
 * 
 * 
 */
public class OracleJdbcWriter extends Writer {

	private Logger logger = Logger.getLogger(OracleJdbcWriter.class);

	private String password;

	private String username;

	private String dbname;

	private String table;

	private String pre;

	private String post;

	private String encoding;

	private String dtfmt;

	private String colorder;

	private int limit;

	private int failCount;

	private long concurrency;

	private int commitCount;

	private String sourceUniqKey = "";

	private String port;

	private String insert;

	private String host;

	private String DRIVER_NAME = "oracle.jdbc.OracleDriver";

	private Connection connection;

	private List<Line> duplicatedLineBuffer;

	private int duplicatedThreshold;

	private String onDuplicatedSql;

	private String duplidatedKeyIndices;

	private String schema;
	
	private String racURL;

	@Override
	public int init() {
		password = param.getValue(ParamKey.password, "");
		username = param.getValue(ParamKey.username, "");
		host = param.getValue(ParamKey.ip);
		port = param.getValue(ParamKey.port, "3306");
		dbname = param.getValue(ParamKey.dbname, "");
		table = param.getValue(ParamKey.table, "");
		schema = param.getValue(ParamKey.schema, "");
		pre = param.getValue(ParamKey.pre, "");
		post = param.getValue(ParamKey.post, "");
		insert = param.getValue(ParamKey.insert, "");
		encoding = param.getValue(ParamKey.encoding, "UTF-8");
		dtfmt = param.getValue(ParamKey.dtfmt, "");
		colorder = param.getValue(ParamKey.colorder, "");
		limit = param.getIntValue(ParamKey.limit, 1000);
		concurrency = param.getIntValue(ParamKey.concurrency, 1);
		duplicatedThreshold = param.getIntValue(ParamKey.duplicatedThreshold,
				10000);
		onDuplicatedSql = param.getValue(ParamKey.onDuplicatedSql, "");
		duplidatedKeyIndices = param
				.getValue(ParamKey.duplicatedKeyIndices, "");

		this.duplicatedLineBuffer = new ArrayList<Line>();

		commitCount = param.getIntValue(ParamKey.commitCount, 50000);
		this.host = param.getValue(ParamKey.ip);
		this.port = param.getValue(ParamKey.port, "3306");
		this.dbname = param.getValue(ParamKey.dbname);
		this.racURL=param.getValue(ParamKey.racUrl,"");
		if (!StringUtils.isBlank(this.racURL)){
			this.sourceUniqKey=DBSource.genKey(this.getClass(), "127.0.0.1", "1521", "test");
		}else{
			this.sourceUniqKey = DBSource.genKey(this.getClass(), host, port,dbname);
		}

		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int prepare(PluginParam param) {
		this.setParam(param);
		DBSource.register(this.sourceUniqKey, this.genProperties());

		if (StringUtils.isBlank(this.pre))
			return PluginStatus.SUCCESS.value();
		
		//执行存储过程
		if (this.pre.startsWith("call")) {
			return execProc(0,this.pre);
		}

		Statement stmt = null;
		try {
			this.connection = DBSource.getConnection(this.sourceUniqKey);

			stmt = this.connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			for (String subSql : this.pre.split(";")) {
				this.logger.info(String.format("执行同步前的sql %s .", subSql));
				stmt.execute(subSql);

			}
			this.connection.commit();
			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			throw new DataExchangeException(e.getCause());
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != this.connection) {
					this.connection.close();
					this.connection = null;
				}
			} catch (SQLException e) {
			}
		}
	}

	@Override
	public int connect() {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startWrite(LineReceiver receiver) {
		PreparedStatement ps = null;
		try {
			this.connection = DBSource.getConnection(this.sourceUniqKey);

			this.logger.info(String.format("配置编码 %s .", this.encoding));

			/* load data begin */
			Line line = null;
			int lines = 0;
			if (StringUtils.isEmpty(this.insert)) {
				this.insert = this.buildInsertString();
			}
			logger.info("sql=" + insert);
			ps = this.connection.prepareStatement(this.insert,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			this.connection.setAutoCommit(false);
			while ((line = receiver.getFromReader()) != null) {
				try {
					ArrayList<String> arrayList=new ArrayList<String>();
					for (int i = 0; i < line.getFieldNum(); i++) {
						ps.setObject(i + 1, line.getField(i));
						arrayList.add(line.getField(i));
					}
					ps.execute();
				} catch (SQLException e) {
					if (e.getMessage().contains("ORA-00001")) {// unique
																// constraint
																// violated
						logger.debug("找到重复行:" + line);
						duplicatedLineBuffer.add(line);
						if (this.duplicatedLineBuffer.size() >= this.duplicatedThreshold) {
							logger.info("太多的重复行，现在处理 .");
							this.connection.commit();
							this.flushDuplicatedBuffer();
						}
					} else {
						failCount++;
						logger.debug("失败行(" + e.getMessage() + "):" + line);
						if (failCount >= this.limit) {
							throw new DataExchangeException("太多失败行("
									+ failCount + ") .");
						} else {
							continue;
						}
					}
				}
				if (lines++ == this.commitCount) {
					logger.info(lines + " 工作线程提交 "
							+ Thread.currentThread().getName() + " .");
					lines = 0;
					this.connection.commit();

				}
			}
			this.connection.commit();
			if (!this.duplicatedLineBuffer.isEmpty()) {
				logger.info("现在处理一些重复行.");
				this.flushDuplicatedBuffer();
			}

			this.connection.setAutoCommit(true);
			this.getMonitor().setFailedLines(this.failCount);
			this.logger.info("插入数据到oracle结束,工作线程： "
					+ Thread.currentThread().getName() + " .");

			return PluginStatus.SUCCESS.value();
		} catch (Exception e2) {
			e2.printStackTrace();
			if (null != this.connection) {
				try {
					this.connection.close();
				} catch (SQLException e) {
				}
			}
			throw new DataExchangeException(e2.getCause());
		} finally {
			if (null != ps){
				try {
					ps.close();
				} catch (SQLException e3) {
				}
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(ExceptionTracker.trace(e));
				}
				connection = null;
			}
		}
	}

	/**
	 * 先删除主键冲突的，再flush缓存中的行到db
	 * 
	 * @throws SQLException
	 */
	private void flushDuplicatedBuffer() throws SQLException {
		if (this.onDuplicatedSql == null || this.onDuplicatedSql.isEmpty()) {
			throw new DataExchangeException("重复处理的sql为空,处理失败.");
		}
		Iterator<Line> lines = this.duplicatedLineBuffer.iterator();
		PreparedStatement ps = null;
		try {
			ps = this.connection.prepareStatement(this.onDuplicatedSql);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("准备重复处理的sql出错.");
			throw new DataExchangeException(e);
		}
		String[] idxs = StringUtils.split(this.duplidatedKeyIndices, ',');
		int[] iidxs = new int[idxs.length];
		for (int i = 0; i < idxs.length; i++) {
			iidxs[i] = Integer.parseInt(idxs[i]);
		}
		int deleteCount = 0;
		int deleteSuccessCount = 0;
		while (lines.hasNext()) {
			Line line = lines.next();
			try {
				for (int i = 0; i < idxs.length; i++) {
					ps.setObject(i + 1, line.getField(iidxs[i]));
					int num = ps.executeUpdate();
					deleteSuccessCount += num;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				// delete failed remove this line
				lines.remove();
				failCount++;
				if (failCount >= this.limit) {
					throw new DataExchangeException("太多失败行(" + failCount
							+ ") .");
				} else {
					continue;
				}
			}
			if (deleteCount++ >= this.commitCount) {
				this.connection.commit();
				deleteCount = 0;
				logger.info("删除 " + deleteCount + " 条重复行 .");
			}
		}
		logger.info(deleteSuccessCount + "/" + this.duplicatedLineBuffer.size()
				+ " 重复行已经删除 .");
		this.connection.commit();
		ps.close();
		ps = this.connection.prepareStatement(this.insert);
		lines = this.duplicatedLineBuffer.iterator();
		int linesCount = 0;
		int insertSuccessCount = 0;
		while (lines.hasNext()) {
			Line line = lines.next();
			try {
				for (int i = 0; i < line.getFieldNum(); i++) {
					ps.setObject(i + 1, line.getField(i));
				}
				ps.execute();
				insertSuccessCount++;
			} catch (SQLException e) {
				e.printStackTrace();
				failCount++;
				if (failCount >= this.limit) {
					throw new DataExchangeException("太多失败行(" + failCount
							+ ") .");
				} else {
					continue;
				}
			}
			if (linesCount++ == this.commitCount) {
				logger.info(lines + " 工作线程提交 "
						+ Thread.currentThread().getName() + " 在重复行删除后.");
				linesCount = 0;
				this.connection.commit();

			}
		}
		ps.close();
		this.connection.commit();
		logger.info(insertSuccessCount + "/" + this.duplicatedLineBuffer.size()
				+ " duplicated line(s) are inserted again .");
		this.duplicatedLineBuffer.clear();
	}

	private int execProc(int type,String strSql) {
		this.connection = DBSource.getConnection(this.sourceUniqKey);
		CallableStatement stmt = null;
		String sql = String.format("{%s}",strSql);
		try {
			stmt = this.connection.prepareCall(sql);
//			stmt.setInt(1, 6);
//			stmt.setString(2, "test");
//			stmt.setInt(3, 45);
			this.logger.info(String.format("执行同步 %s 的sql %s .",type==0 ? "前":"后",strSql));
			stmt.execute();
			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			e.printStackTrace();
			return PluginStatus.FAILURE.value();
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != this.connection) {
					this.connection.close();
					this.connection = null;
				}
			} catch (Exception e2) {
			}

		}
	}

	@Override
	public int post(PluginParam param) {
		if (StringUtils.isBlank(this.post))
			return PluginStatus.SUCCESS.value();
		
		//执行存储过程
		if (this.post.startsWith("call")) {
			return execProc(1,this.post);
		}

		Statement stmt = null;
		try {
			this.connection = DBSource.getConnection(this.sourceUniqKey);

			stmt = this.connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			for (String subSql : this.post.split(";")) {
				this.logger.info(String.format("执行同步后的sql %s .", subSql));
				stmt.execute(subSql);
			}

			return PluginStatus.SUCCESS.value();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataExchangeException(e.getCause());
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != this.connection) {
					this.connection.close();
					this.connection = null;
				}
			} catch (Exception e2) {
			}

		}
	}

	@Override
	public List<PluginParam> split(PluginParam param) {
		// OracleThinWriterSplitter splitter = new OracleThinWriterSplitter();
		OracleJdbcWriterSplitter splitter = new OracleJdbcWriterSplitter();
		splitter.setParam(param);
		splitter.init();
		return splitter.split();
	}

	@Override
	public int commit() {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int finish() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(ExceptionTracker.trace(e));
			}
			connection = null;
		}
		
		return PluginStatus.SUCCESS.value();
	}
	
	@Override
	public int cleanup() {
		DBSource.close(this.sourceUniqKey);
		return PluginStatus.SUCCESS.value();
	}

	private Properties genProperties() {
		Properties p = new Properties();
		p.setProperty("driverClassName", this.DRIVER_NAME);
		String url="";
		if (!StringUtils.isBlank(this.racURL)){
			url="jdbc:oracle:thin:@"+this.racURL;
		}else {
			url = "jdbc:oracle:thin:@" + this.host + ":" + this.port + "/"
					+ this.dbname;
		}
		
		p.setProperty("url", url);
		p.setProperty("username", this.username);
		p.setProperty("password", this.password);
		p.setProperty("maxActive", String.valueOf(this.concurrency + 2));

		return p;
	}

	public String buildInsertString() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(this.schema + "." + this.table)
				.append(" ");
		if (!StringUtils.isEmpty(this.colorder)) {
			sb.append("(").append(this.colorder).append(")");
		}
		sb.append(" VALUES(");
		try {
			ResultSet rs = this.connection.createStatement().executeQuery(
					"SELECT COLUMN_NAME,DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME='"
							+ this.table.toUpperCase() + "'");
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			while (rs.next()) {
				String colName = rs.getString(1);
				String colType = rs.getString(2);
				map.put(colName, colType);
			}
			logger.debug("Column map:size=" + map.size() + ";cols="
					+ map.toString());
			if (StringUtils.isEmpty(this.colorder)) {
				Iterator<Entry<String, String>> it = map.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String colType = entry.getValue();
					if (colType.toUpperCase().equals("DATE")) {
						sb.append("to_date(?,'" + this.dtfmt + "'),");
					} else {
						sb.append("?,");
					}
				}
				sb.deleteCharAt(sb.length() - 1);// remove last comma
				sb.append(")");
			} else {
				String[] arr = colorder.toUpperCase().split(",");
				for (String colName : arr) {
					if (!map.containsKey(colName.trim())) {
						throw new DataExchangeException("列 " + colName
								+ " 不在数据表中");
					}
					String colType = map.get(colName.trim());
					if (colType.toUpperCase().equals("DATE")) {
						sb.append("to_date(?,'" + this.dtfmt + "'),");
					} else {
						sb.append("?,");
					}
				}
				sb.deleteCharAt(sb.length() - 1);// remove last comma
				sb.append(")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataExchangeException(e.getMessage());
		}

		return sb.toString();
	}
}
