package com.gci.datax.plugins.common;

import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.Pluginable;
import com.gci.datax.plugins.reader.mysqlreader.MysqlReader;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * 数据源管理类
 * 
 */
public class DBSource {
	private static Logger logger = Logger.getLogger(DBSource.class);

	public static HashMap<String, DataSource> sourceInfoMap = new HashMap<String, DataSource>();

	private DBSource() {
	}

	public static boolean register(Class<? extends Pluginable> clazz, String ip,
			String port, String dbname, Properties p) {

		String id = genKey(clazz, ip, port, dbname);

		return register(id, p);
	}

	public static synchronized boolean register(String key, Properties p) {
		boolean succeed = false;

		if (!sourceInfoMap.containsKey(key)) {
			BasicDataSource dataSource = null;
			try {
				dataSource = (BasicDataSource) BasicDataSourceFactory
						.createDataSource(p);
			} catch (Exception e) {
				logger.error(String.format(
						"Key [%s] register database pool failed .", key));
				throw new IllegalStateException(e.getCause());
			}
			if (null != dataSource) {
				dataSource.setAccessToUnderlyingConnectionAllowed(true);
				sourceInfoMap.put(key, dataSource);
				logger.info(String.format(
						"Key [%s] 注册数据库连接池成功 .", key));
				succeed = true;
			} else {
				logger.error(String.format(
						"Key [%s] 注册数据库池失败 .", key));
			}
		} else {
			logger.error(String.format("Key [%s] 已经在数据库连接池 .",
					key));
		}

		return succeed;
	}
	
	public static synchronized boolean close(String key) {
		boolean succeed=false;
		if (sourceInfoMap.containsKey(key)){
			BasicDataSource dataSource = (BasicDataSource) sourceInfoMap.get(key);
			try {
				sourceInfoMap.remove(key);
				dataSource.close();
				logger.info("关闭数据源.");
			} catch (SQLException e) {		
				e.printStackTrace();
			}
		}else{
			
		}
		return succeed;
	}

	public static DataSource getDataSource(Class<? extends Pluginable> clazz,
			String ip, String port, String dbname) {
		return getDataSource(genKey(clazz, ip, port, dbname));
	}

	public static synchronized DataSource getDataSource(String key) {
		DataSource source = sourceInfoMap.get(key);
		if (null == source) {
			throw new IllegalArgumentException(String.format(
					"Cannot get DataSource specified by key [%s] .", key));
		}
		return source;
	}


	public static Connection getConnection(Class<? extends Pluginable> clazz,
			String ip, String port, String dbname) {
		return getConnection(genKey(clazz, ip, port, dbname));
	}

	public static synchronized Connection getConnection(String id) {
		Connection c = null;
		BasicDataSource dataSource = (BasicDataSource) sourceInfoMap.get(id);
		try {
			c = dataSource.getConnection();
		} catch (SQLException e) {
			logger.error(ExceptionTracker.trace(e));
			throw new IllegalArgumentException(e.getCause());
		}
		if (null != c) {
			logger.info(String.format(
					"Key [%s] 连接数据库连接池成功 .", id));
		} else {
			logger.error(String.format(
					"Key [%s]  连接数据库连接池失败 .", id));
			throw new IllegalArgumentException(String.format(
					"Connection key [%s] error .", id));
		}
		return c;
	}

	public static String genKey(Class<? extends Pluginable> clazz, String ip,
			String port, String dbname) {
		String str = clazz.getCanonicalName() + "_" + ip + "_" + port + "_"
				+ dbname;
		return md5(str);
	}

	private static String md5(String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer(32);
            for (byte aB : b) {
                i = aB;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
			return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			logger.error(ExceptionTracker.trace(e));
			return key;
		}
	}
}
