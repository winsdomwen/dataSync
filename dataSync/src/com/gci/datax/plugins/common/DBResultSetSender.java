package com.gci.datax.plugins.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.PluginMonitor;
import com.gci.datax.common.plugin.Reader;
import com.gci.datax.common.plugin.Writer;


/**
 * 一个代理类，提供给{@link Reader}插件,包装ResultSet数据到line,并发送给{@link Writer}
 * 
 * @see Reader
 * @see DBResultSetReceiver
 * 
 */
public class DBResultSetSender {
	private LineSender sender;

	protected PluginMonitor monitor;

	private int columnCount;

	private Map<String, SimpleDateFormat> dateFormatMap = new HashMap<String, SimpleDateFormat>();

	private SimpleDateFormat[] timeMap = null;

	private static final Logger logger = Logger.getLogger(DBResultSetSender.class);

	public static DBResultSetSender newSender(LineSender sender) {
		return new DBResultSetSender(sender);
	}

	public DBResultSetSender(LineSender lineSender) {
		this.sender = lineSender;
	}

	public void setMonitor(PluginMonitor iMonitor) {
		this.monitor = iMonitor;
	}
	
	public void setDateFormatMap(Map<String, SimpleDateFormat> dateFormatMap) {
		this.dateFormatMap = dateFormatMap;
	}

	public void sendToWriter(ResultSet resultSet) throws SQLException {
		String item = null;
		Timestamp ts = null;
		setColumnCount(resultSet.getMetaData().getColumnCount());
		setColumnTypes(resultSet);
		while (resultSet.next()) {
			Line line = sender.createLine();
			try {
	
				for (int i = 1; i <= columnCount; i++) {
					if (null != timeMap[i]) {
						ts = resultSet.getTimestamp(i);
						if (null != ts) {
							item = timeMap[i].format(ts);
						} else {
							item = null;
						}
					} else {
						item = resultSet.getString(i);
					}
					line.addField(item);
				}
				boolean b = sender.sendToWriter(line);
				if (null != monitor) {
					if (b) {
						monitor.lineSuccess();
					} else {
						monitor.lineFail("Send one line failed!");
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage() + "| One dirty line : " + line.toString('\t'));
			}
		}
		
	}

	public void flush() {
		if (sender != null) {
			sender.flush();
		}
	}
	
	private void setColumnTypes(ResultSet resultSet) throws SQLException {
		timeMap = new SimpleDateFormat[columnCount + 1];

		ResultSetMetaData rsmd = resultSet.getMetaData();
		
		for (int i = 1; i <= columnCount; i++) {
			String type = rsmd.getColumnTypeName(i).toLowerCase().trim();
			if (this.dateFormatMap.containsKey(type)) {
				timeMap[i] = this.dateFormatMap.get(type);
			}
		}
	}
	
	private void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
}
