package com.gci.datax.plugins.writer.sqlserverwriter;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
import com.gci.datax.plugins.writer.oraclejdbcwriter.OracleJdbcWriterSplitter;
import com.gci.datax.plugins.writer.sqlserverwriter.ParamKey;

public class SqlServerWriter extends Writer{
	
	private Logger logger = Logger.getLogger(SqlServerWriter.class);
	
	private String ip;
	
	private String port;
	
	private String database;
	
	private String username;
	
	private String password;
	
	private String table;
	
	private int concurrency = 1;
	
	private String sql;
	
	private String pre;
	
	private String post;
	
	private String insert;
	
	private String schema;
	
	private String colorder;
	
	private static final String DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private Connection connection;	
	
	@Override
	public int init() {
		// TODO Auto-generated method stub
		this.ip = param.getValue(ParamKey.ip);
		this.port =  param.getValue(ParamKey.port, "1433");
		this.database = param.getValue(ParamKey.dbName, "");
		this.username = param.getValue(ParamKey.username, "");
		this.password = param.getValue(ParamKey.password, "");
		this.sql = param.getValue(ParamKey.sql, "").trim();
		this.table = param.getValue(ParamKey.table, "").trim();
		this.schema = param.getValue(ParamKey.schema, "").trim();
		pre = param.getValue(ParamKey.pre, "");
		post = param.getValue(ParamKey.post, "");
		this.colorder = param.getValue(ParamKey.colorder, "").trim();
		this.concurrency = Integer.parseInt(param.getValue(ParamKey.concurrency, "1"));
		
		return PluginStatus.SUCCESS.value();
	}
	
	@Override
	public int prepare(PluginParam param) {
		try {
			DBSource.register(this.getClass(), this.ip, this.port, this.database, this.genProperties());
		
			if (StringUtils.isBlank(this.pre))
				return PluginStatus.SUCCESS.value();
			
			//执行存储过程
			if (this.pre.startsWith("call")) {
				return execProc(0,this.pre);
			}			
			
			Statement stmt  = this.connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			for (String subSql : this.post.split(";")) {
				this.logger.info(String.format("执行同步数据后的sql %s .",
						subSql));
				stmt.execute(subSql);
			}					
		
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
				
		return PluginStatus.SUCCESS.value();
	}
	
	private Properties genProperties() {
		Properties p = new Properties();
		
		p.setProperty("driverClassName", DRIVER_NAME);
		p.setProperty("url", String.format("jdbc:sqlserver://%s;DatabaseName=%s", this.ip, this.database));
		p.setProperty("username", this.username);
		p.setProperty("password", this.password);
		p.setProperty("maxActive", String.valueOf(concurrency + 2));
		p.setProperty("initialSize", String.valueOf(concurrency + 2));
		p.setProperty("maxIdle", "1");
		p.setProperty("maxWait", "1000");
		p.setProperty("defaultReadOnly", "true");
		
		return p;
	}
	@Override
	public int connect() {
		// TODO Auto-generated method stub
		try {
			this.connection = DBSource.getConnection(this.getClass(), this.ip, this.port, this.database);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
		return PluginStatus.SUCCESS.value();
	}	

	@Override
	public int commit() {
		// TODO Auto-generated method stub
		return PluginStatus.SUCCESS.value();
	}


	@Override
	public int finish() {
		// TODO Auto-generated method stub
		try {
			if (null != this.connection)
				this.connection.close();
		} catch (SQLException e) {
			//swallow
		}
		return PluginStatus.SUCCESS.value();
	}



	@Override
	public int startWrite(LineReceiver receiver) {
		// TODO Auto-generated method stub
		
		Line line = null;
		
		PreparedStatement ps = null;		
		
		if (StringUtils.isEmpty(this.insert)) {
			this.insert = this.buildInsertString();
		}
		
		try {			
			
			logger.info("sql=" + this.insert);
			ps = this.connection.prepareStatement(this.insert);
			
			while ((line = receiver.getFromReader()) != null) {
				try {
					ArrayList<String> arrayList = new ArrayList<String>();
					for (int i = 0; i < line.getFieldNum(); i++) {
						String data = line.getField(i);						
						ps.setObject(i + 1, data);
						arrayList.add(data);
					}
					ps.execute();
				} catch (SQLException e) {
					//logger.error(ExceptionTracker.trace(e));
					logger.error(e.getMessage());
										
				}				
			}
			this.connection.commit();
			
			
		}catch (Exception e2) {
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
		
		return PluginStatus.SUCCESS.value();
	}
	
	
	/**
	 * 生成插入的sql
	 * @return
	 */
	public String buildInsertString() {
		
		StringBuilder sb = new StringBuilder();
		//sb.append("insert  into ").append(this.schema + "." + this.table);
		if("sa".equals(this.schema)||"".equals(this.schema))
			sb.append("insert  into ").append(this.table);
		else
			sb.append("insert  into ").append(this.schema + "." + this.table);
		
		if (!StringUtils.isEmpty(this.colorder)) {
			sb.append("(").append(this.colorder).append(")");
		}

		sb.append(" values(");		
		
		String[] arr = colorder.split(",");
		for (int i=0; i < arr.length;i++) {
				sb.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);// remove last comma
		sb.append(")");						
		
		return sb.toString();
	
	}
	
	
	private int execProc(int type,String strSql) {
		//this.connection = DBSource.getConnection(this.sourceUniqKey);
		CallableStatement stmt = null;
		String sql = String.format("{%s}",strSql);
		try {
			stmt = this.connection.prepareCall(sql);
			this.logger.info(String.format("执行同步 %s 的sql %s .",type==0 ? "前":"后",strSql));
			return stmt.execute() ? PluginStatus.SUCCESS.value():PluginStatus.FAILURE.value();
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
		SqlServerWriterSplitter splitter = new SqlServerWriterSplitter();
		splitter.setParam(param);
		splitter.init();
		return splitter.split();
	}

}
