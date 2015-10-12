package com.gci.datax.plugins.common;

import com.gci.datax.common.plugin.*;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static java.text.MessageFormat.format;


/**
 * 一个代理类，提供给{@link Writer}插件,写数据到目标数据源
 * @see Reader
 * @see DBResultSetReceiver
 * 
 */
public class DBResultSetReceiver {
	
	private LineReceiver receiver;

	protected PluginMonitor monitor;
	
	private int columnCount;

	private static final Logger logger = Logger.getLogger(DBResultSetSender.class);
	
	public static DBResultSetReceiver newProxy(LineReceiver receiver) {
		return new DBResultSetReceiver(receiver);
	}

	public DBResultSetReceiver(LineReceiver receiver) {
		this.receiver = receiver;
	}
	
	public void setMonitor(PluginMonitor iMonitor) {
		this.monitor = iMonitor;
	}
	
	private void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public void receiverFromReader(LineReceiver receiver, Connection conn, MetaData meta) throws SQLException {
		//PreparedStatement ps = conn.prepareStatement("");
	}
	
	public void getFromReader(LineReceiver receiver,String INSERT_PATTERN,Statement stmt,ResultSetMetaData meta,boolean b) throws SQLException{
		this.setColumnCount(meta.getColumnCount());	
		if(b){
			//表示insert语句模式为：insert into tableName values {0}
			Line line = null;
			String sql = "";
			while ((line = receiver.getFromReader()) != null) {
				StringBuilder valuseAfter = new StringBuilder('(');
				for(int i=1;i<columnCount;i++){
					valuseAfter.append("'").append(line.getField(i)).append("'");
					if(i!=columnCount-1){
						valuseAfter.append(",");
					}
				}
				valuseAfter.append(')');
				sql = format(INSERT_PATTERN,valuseAfter.toString());
				stmt.executeUpdate(sql);
			}
		}else{
			//表示insert语句模式为：insert into tableName {0} values {1}
			StringBuilder valuseBefore = new StringBuilder("(");
			for(int i=0;i<columnCount;i++){
				valuseBefore.append("'").append(meta.getColumnName(i)).append("'");
				if(i!=columnCount-1){
					valuseBefore.append(",");
				}
			}
			valuseBefore.append(")");
			
			Line line = null;
			String sql = "";
			while ((line = receiver.getFromReader()) != null) {
				StringBuilder valuseAfter = new StringBuilder('(');
				for(int i=1;i<columnCount;i++){
					valuseAfter.append("'").append(line.getField(i)).append("'");
					if(i!=columnCount-1){
						valuseAfter.append(",");
					}
				}
				valuseAfter.append(')');
				
				sql = format(INSERT_PATTERN,valuseBefore.toString(),valuseAfter.toString());
				stmt.executeUpdate(sql);
			}
		}
	}
	
}
