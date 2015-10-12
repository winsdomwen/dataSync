package com.gci.datax.plugins.reader.oraclereader;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.*;
import com.gci.datax.plugins.common.DBResultSetSender;
import com.gci.datax.plugins.common.DBSource;
import com.gci.datax.plugins.common.DBUtils;
import com.gci.datax.plugins.common.OracleTnsAssist;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class OracleReader extends Reader {
	public static final String ORACLE_READER_DB_POOL_KEY = "ORACE_DB_POOL_KEY";
	
	private Connection connection;

	private String username = "";

	private String password = "";

	private String ip;

	private String port = "1521";

	private String dbname;

	private String sql;

	private String charset = "utf-8";

	private String tnsname;
	
	private String racURL;
	
	private int concurrency;
	
	private int splitMode;

	private String connectKey;

	private static final Set<String> supportEncode = new HashSet<String>() {
		{
			add("utf-8");
			add("gbk");
			add("gb2312");
		}
	};

	private Logger logger = Logger.getLogger(OracleReader.class);

	@Override
	public int init() {
		this.sql = param.getValue(ParamKey.sql, "").trim();
		
		//oracle jdbc driver必须确保 sql不能以';'结束
		if (this.sql.endsWith(";")) {
			this.sql = this.sql.substring(0, this.sql.lastIndexOf(';'));
			param.putValue(ParamKey.sql, this.sql);
		}
		
		this.charset = param
				.getValue(ParamKey.encoding, "utf-8")
				.toLowerCase().trim();
		if (!isSupportEncode(charset)) {
			throw new DataExchangeException("encoding error");
		}

		this.ip = param.getValue(ParamKey.ip, "");
		this.port = param.getValue(ParamKey.port, this.port);
		this.username = param.getValue(ParamKey.username, this.username);
		this.password = param.getValue(ParamKey.password, this.password);
		this.dbname = param.getValue(ParamKey.dbname);
		this.tnsname = param.getValue(ParamKey.tnsFile, "");
		this.racURL=param.getValue(ParamKey.racUrl,"");
		this.splitMode = param.getIntValue(ParamKey.splitMod, 1);
		this.concurrency = param.getIntValue(ParamKey.concurrency, 1);
		this.connectKey = param.getValue(ORACLE_READER_DB_POOL_KEY, "oracle_pool_key");
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int prepare(PluginParam param) {
		if (!StringUtils.isBlank(this.racURL)){
			this.connectKey=DBSource.genKey(this.getClass(), "127.0.0.1", "1521", "test");
			DBSource.register(this.connectKey, this.createProperties());
			this.connection=DBSource.getConnection(this.connectKey);
		}
		else if (!StringUtils.isBlank(this.ip) && !StringUtils.isBlank(this.port)) {
			this.connectKey = DBSource.genKey(this.getClass(), this.ip, this.port, this.dbname);
			DBSource.register(this.connectKey, this.createProperties());
			this.connection = DBSource.getConnection(this.connectKey);
		} else {
			List<Map<String, String>> details;
			try {
				details = OracleTnsAssist.newInstance(
						tnsname).search(dbname);
			} catch (IOException e1) {
				throw new DataExchangeException(e1.getCause());
			}
			
			for (Map<String, String> kv : details) {
				this.ip = kv.get(OracleTnsAssist.HOST_KEY);
				this.port = kv.get(OracleTnsAssist.PORT_KEY);
				this.dbname = kv.get(OracleTnsAssist.SID_KEY);
				this.connectKey = DBSource.genKey(this.getClass(), ip, port, dbname);
				try {
					DBSource.register(this.connectKey, this.createProperties());
					this.connection = DBSource.getConnection(this.connectKey);
				} catch (Exception e) {
					logger.error(String.format(
							"OracleReader try to connect %s:%s failed ",
							this.ip, this.port));
					continue;
				}
				if (null != this.connection) {
					break;
				}
			}
		}
		
		if (null == this.connection) {
			String msg = "OracleReader尝试连接数据库 .";
			logger.error(msg);
			throw new DataExchangeException(msg);
		}
		
		param.putValue(ORACLE_READER_DB_POOL_KEY, this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split(PluginParam param) {
		List<PluginParam> params = null;		
		if (!StringUtils.isBlank(this.sql)) {
			params = super.split(param);
		} else {
			Splitter spliter;
			switch (this.splitMode) {
			case 0:
				logger.info("OracleReader use no-rowid split mechanism .");
				spliter = new OracleReaderTableSplitter(param);
				break;
			
			case 1:
				logger.info("OracleReader use rowid split mechanism .");
				spliter = new OracleReaderRowidSplitter(param);
				break;
				
			default:
				logger.info("OracleReader use ntile-rowid split mechanism .");
				spliter = new OracleReaderRowidSplitter(param);
				break;
			}
			spliter.init();
			params = spliter.split();
		}
		
		String sql = params.get(0).getValue(ParamKey.sql);
		MetaData m = null;
		try {
			m = DBUtils.genMetaData(connection, sql);
			param.setMyMetaData(m);
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
		
		return params;
	}

	@Override
	public int connect() {
		connection = DBSource.getConnection(this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startRead(LineSender lineSender) {
		DBResultSetSender proxy = DBResultSetSender.newSender(lineSender);
		proxy.setMonitor(getMonitor());
		proxy.setDateFormatMap(genDateFormatMap());

		String sql = param.getValue(ParamKey.sql);
		logger.info(String.format("OracleReader开始查询 %s .", sql));
		ResultSet rs = null;
		try {
			rs = DBUtils.query(connection, sql);
			proxy.sendToWriter(rs);
            proxy.flush();
            getMonitor().setStatus(PluginStatus.READ_OVER);
            return PluginStatus.SUCCESS.value();
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		} finally {
            if (null != rs) {
                DBUtils.closeResultSet(rs);
            }
        }


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
		DBSource.close(this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	private boolean isSupportEncode(String encode) {
		if (supportEncode.contains(encode.toLowerCase())) {
			return true;
		}
		return false;
	}

	private Map<String, SimpleDateFormat> genDateFormatMap() {
		Map<String, SimpleDateFormat> mapDateFormat;
		mapDateFormat = new HashMap<String, SimpleDateFormat>();
		mapDateFormat.clear();
		mapDateFormat.put("date", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss"));
		mapDateFormat.put("timestamp", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss"));
		return mapDateFormat;
	}

	private Properties createProperties() {
		Properties p = new Properties();
		String url="";
		if (!StringUtils.isBlank(this.racURL)){
			url="jdbc:oracle:thin:@"+this.racURL;
		}else {
			url = "jdbc:oracle:thin:@" + this.ip + ":" + this.port + "/"
					+ this.dbname;
		}


		p.setProperty("driverClassName", "oracle.jdbc.OracleDriver");
		p.setProperty("url", url);
		p.setProperty("username", this.username);
		p.setProperty("password", this.password);

		p.setProperty("maxActive", String.valueOf(concurrency + 2));
		p.setProperty("initialSize", String.valueOf(concurrency + 2));
		p.setProperty("maxIdle", "1");
		p.setProperty("maxWait", "1000");
		p.setProperty("defaultReadOnly", "true");
		p.setProperty("testOnBorrow", "true");
		p.setProperty("validationQuery", "select 1 from dual");

		this.logger.info(String.format("OracleReader尝试连接: %s .", url));
		return p;
	}
}
