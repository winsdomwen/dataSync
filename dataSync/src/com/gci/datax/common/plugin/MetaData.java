package com.gci.datax.common.plugin;

import java.util.List;

/**
 * 数据源的元数据信息。
 * 
 * */
public class MetaData {
	private String dataBaseName;

	private String dataBaseVersion;

	private String tableName = "default_table";
	
	private List<Column> colInfo;
	

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public String getDataBaseVersion() {
		return this.dataBaseVersion;
	}

	public void setDataBaseVersion(String dataBaseVersion) {
		this.dataBaseVersion = dataBaseVersion;
	}

	/**
	 * 获取所有列信息
	 * 
	 * @return		
	 * 			列信息列表
	 * 
	 * */
	public List<Column> getColInfo() {
		return colInfo;
	}

	/**
	 * 设置列信息
	 * 
	 * @param	colInfo
	 * 			
	 * 
	 * */
	public void setColInfo(List<Column> colInfo) {
		this.colInfo = colInfo;
	}

	public class Column {
		private boolean isText = false;

		private boolean isNum = false;

		private String colName;

		private String DataType; 

		private boolean isPK;

		public String getDataType() {
			return DataType;
		}

		public String getColName() {
			return colName;
		}

		public void setDataType(String dataType) {
			DataType = dataType;
		}

		public boolean isPK() {
			return isPK;
		}

		public void setPK(boolean isPK) {
			this.isPK = isPK;
		}

		public boolean isText() {
			return isText;
		}

		public void setText(boolean isText) {
			this.isText = isText;
		}

		public boolean isNum() {
			return isNum;
		}

		public void setNum(boolean isNum) {
			this.isNum = isNum;
		}

		public void setColName(String name) {
			colName = name;
		}
	}
}
