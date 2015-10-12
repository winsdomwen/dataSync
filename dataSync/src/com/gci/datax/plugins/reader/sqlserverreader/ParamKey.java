package com.gci.datax.plugins.reader.sqlserverreader;

public class ParamKey {
	 /*
     * @name: ip
     * @description: Sqlserver database's ip address
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String ip = "ip";
	/*
     * @name: port
     * @description: Sqlserver database's port
     * @range:
     * @mandatory: false
     * @default:
     */
	public final static String port = "port";
	/*
     * @name: dbname
     * @description: Sqlserver database's name
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String dbName = "dbname";
	/*
     * @name: username
     * @description: Sqlserver database's login name
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String username = "username";
	/*
     * @name: password
     * @description: Sqlserver database's login password
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String password = "password";
	/*
     * @name: tables
     * @description: tables to export data, format can support simple regex, table[0-63]
     * @range: 
     * @mandatory: true
     * @default: 
     */
	public final static String tables = "tables";
	/*
     * @name: where
     * @description: where clause, like 'modified_time > sysdate'
     * @range: 
     * @mandatory: true
     * @default: 
     */
	public final static String where = "where";
	/*
     * @name: columns
     * @description: columns to be selected, default is *
     * @range: 
     * @mandatory: true
     * @default: *
     */
	public final static String columns = "columns";
	
	/*
     * @name: sql
     * @description: self-defined sql statement
     * @range: 
     * @mandatory: false
     * @default: 
     */
	public final static String sql = "sql";
	
	/*
     * @name: ENCODING
     * @description: Sqlserver database's encode
     * @range: UTF-8|GBK|GB2312
     * @mandatory: false
     * @default: UTF-8
     */
	public final static String encoding = "encoding";
  /*
     * @name: sqlserver.params
     * @description: Sqlserver driver params
     * @range: 
     * @mandatory: false
     * @default:
     */
	public final static String sqlServerParams = "sqlserver.params";
	
	 /*
     * @name:concurrency
     * @description:concurrency of the job
     * @range:1-100
     * @mandatory: false
     * @default:1
     */
	public final static String concurrency = "concurrency";
}
