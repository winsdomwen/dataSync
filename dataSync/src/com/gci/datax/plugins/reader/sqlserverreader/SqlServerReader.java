package com.gci.datax.plugins.reader.sqlserverreader;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.plugins.common.DBResultSetSender;
import com.gci.datax.plugins.common.DBSource;
import com.gci.datax.plugins.common.DBUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 如果配置文件中指定了sql，则无视其他查询配置（包括tables,where,limit,columns），直接执行sql
 * 如果配置文件中未指定sql，则根据其他查询配置信息自动生成sql
 * 该特性可以可以非常好地支持复杂的用户自定义查评
 */
public class SqlServerReader extends Reader {
	private Logger logger = Logger.getLogger(SqlServerReader.class);
	
	private String ip;
	
	private String port;
	
	private String database;
	
	private String username;
	
	private String password;
	
	private int concurrency = 1;
	
	private String sql;
	
	private static final String DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private Connection connection;
	
	@Override
	public int init(){
		this.ip = param.getValue(ParamKey.ip);
		this.port =  param.getValue(ParamKey.port, "1433");
		this.database = param.getValue(ParamKey.dbName, "");
		this.username = param.getValue(ParamKey.username, "");
		this.password = param.getValue(ParamKey.password, "");
		this.sql = param.getValue(ParamKey.sql, "").trim();
		this.concurrency = Integer.parseInt(param.getValue(ParamKey.concurrency, "1"));
		
		return PluginStatus.SUCCESS.value();
	}
	
	@Override
	public int prepare(PluginParam param) {
		try {
			DBSource.register(this.getClass(), this.ip, this.port, this.database, this.genProperties());
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
	public int connect(){
		try {
			this.connection = DBSource.getConnection(this.getClass(), this.ip, this.port, this.database);
		} catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startRead(LineSender sender){
		if(null == this.connection){
			logger.error("Connect to Sqlserver failed .");
			return PluginStatus.FAILURE.value();
		}
		
		DBResultSetSender proxy = DBResultSetSender.newSender(sender);
		proxy.setMonitor(this.getMonitor());
		proxy.setDateFormatMap(this.getMapDateFormat());
		
		logger.info(String.format("Execute sql %s .", this.sql));

        ResultSet rs = null;
        try {
			rs = DBUtils.query(this.connection, sql);
			proxy.sendToWriter(rs);
			proxy.flush();
            getMonitor().setStatus(PluginStatus.READ_OVER);
            return PluginStatus.SUCCESS.value();
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		} finally {
            if (null != rs) {
                DBUtils.closeResultSet(rs);
            }
        }
	}

	private Map<String, SimpleDateFormat> getMapDateFormat() {
		Map<String,SimpleDateFormat> mapDateFormat = new HashMap<String,SimpleDateFormat>();
		mapDateFormat.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapDateFormat.put("smalldatetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		return mapDateFormat;
	}

	@Override
	public int finish() {
		try {
			if (null != this.connection)
				this.connection.close();
		} catch (SQLException e) {
			//swallow
		}
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split(PluginParam param){
		List<PluginParam> params;
		
		if (StringUtils.isBlank(this.sql)) {
			logger.info("No user-defined sql found, begin to construct sql-statement .");
			params = new SqlServerSplitter(param).split();
		} else {
			logger.info(String.format("User-defined sql [%s] found", sql));
			params = super.split(param);
		}
		
		String singleSql = params.get(0).getValue(ParamKey.sql);
		try {
			this.connect();
			param.setMyMetaData(DBUtils.genMetaData(this.connection, singleSql));
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
		return params;
	}
}
