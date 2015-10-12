package com.gci.datax.plugins.reader.mysqlreader;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.*;
import com.gci.datax.plugins.common.DBResultSetSender;
import com.gci.datax.plugins.common.DBSource;
import com.gci.datax.plugins.common.DBUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MysqlReader extends Reader {
	public static final String MYSQL_READER_DB_POOL_KEY = "MYSQL_DB_POOL_KEY";
	private Connection conn;

	private String encode = null;

	private String username = "";

	private String password = "";

	private String ip = "";

	private String port = "3306";

	private String dbname = null;

	private int concurrency = -1;

	private String mysqlParams;
	
	private String sql = null;
	
	private String connectKey;

	private static final Set<String> supportEncode = new HashSet<String>() {
		{
			add("utf-8");
			add("gbk");
		}
	};

	private Logger logger = Logger.getLogger(MysqlReader.class);

	@Override
	public int init() {
		this.username = param.getValue(ParamKey.username, this.username);
		this.password = param.getValue(ParamKey.password, this.password);
		this.ip = param.getValue(ParamKey.ip);
		this.port = param.getValue(ParamKey.port, this.port);
		this.dbname = param.getValue(ParamKey.dbname);
		this.encode = param.getValue(ParamKey.encoding, "");
		this.mysqlParams = param.getValue(ParamKey.mysqlParams,"");
		
		this.sql = param.getValue(ParamKey.sql, "").trim();

		this.concurrency = param.getIntValue(ParamKey.concurrency, 1);
		
		this.connectKey = param.getValue(MYSQL_READER_DB_POOL_KEY, "oracle_pool_key");
		
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int prepare(PluginParam param) {
		this.init();

		this.connectKey = DBSource.genKey(this.getClass(), this.ip, this.port, this.dbname);
		DBSource.register(this.connectKey, this.createProperties());
		this.conn = DBSource.getConnection(this.connectKey);
		
		if (null == this.conn) {
			String msg = "MysqlReader尝试连接数据库 .";
			logger.error(msg);
			throw new DataExchangeException(msg);
		}

		param.putValue(MYSQL_READER_DB_POOL_KEY, this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int connect() {
		conn = DBSource.getConnection(this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startRead(LineSender lineSender){
		DBResultSetSender proxy = DBResultSetSender.newSender(lineSender);
		proxy.setMonitor(getMonitor());
		proxy.setDateFormatMap(genDateFormatMap());
		
		String sql = param.getValue(ParamKey.sql);
		this.logger.info(String.format("MysqlReader开始查询 %s .", sql));
		ResultSet rs = null;
		try {
			rs = DBUtils.query(conn, sql);
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
	public int finish(){
		try {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		} catch (SQLException e) {
		}
		DBSource.close(this.connectKey);
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split(PluginParam param){
		List<PluginParam> sqlList;
		
		if (StringUtils.isBlank(this.sql)) {
			MysqlReaderSplitter spliter = new MysqlReaderSplitter(param);
			spliter.init();
			sqlList = spliter.split();
		} else {
			sqlList = super.split(param);
		}
		
		String sql = sqlList.get(0).getValue(ParamKey.sql);
		MetaData m = null;
		try {
			m = DBUtils.genMetaData(conn, sql);
			param.setMyMetaData(m);
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e);
		}
		
		return sqlList;
	}

	private Map<String, SimpleDateFormat> genDateFormatMap() {
		Map<String, SimpleDateFormat> mapDateFormat = new HashMap<String, SimpleDateFormat>();
		mapDateFormat.clear();
		mapDateFormat.put("datetime", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss"));
		mapDateFormat.put("timestamp", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss"));
		mapDateFormat.put("time", new SimpleDateFormat("HH:mm:ss"));
		return mapDateFormat;
	}

	private boolean isSupportEncode(String encode) {
		if (supportEncode.contains(encode.toLowerCase())) {
			return  true;
		}
		return false;
	}

	private Properties createProperties() {
		Properties p = new Properties();
		
		String encodeDetail = "";
		
		if(!StringUtils.isBlank(this.encode)){
			encodeDetail = "useUnicode=true&characterEncoding="	+ this.encode + "&";
		}
		String url = "jdbc:mysql://" + this.ip + ":" + this.port + "/"
				+ this.dbname + "?" + encodeDetail 
				+ "yearIsDateType=false&zeroDateTimeBehavior=convertToNull"
				+ "&defaultFetchSize=" + String.valueOf(Integer.MIN_VALUE);
		
		if (!StringUtils.isBlank(this.mysqlParams)) {
			url = url + "&" + this.mysqlParams;
		}
		
		p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
		p.setProperty("url", url);
		p.setProperty("username", username);
		p.setProperty("password", password);
		p.setProperty("maxActive", String.valueOf(concurrency + 2));
		p.setProperty("initialSize", String.valueOf(concurrency + 2));
		p.setProperty("maxIdle", "1");
		p.setProperty("maxWait", "1000");
		p.setProperty("defaultReadOnly", "true");
		p.setProperty("testOnBorrow", "true");
		p.setProperty("validationQuery", "select 1 from dual");

		this.logger.info(String.format("MysqlReader try connection: %s .", url));
		return p;
	}

}
