package com.gci.datax.plugins.reader.oraclereader;

public final class ParamKey {
	/*
       * @name: dbname
       * @description: Oracle数据库名
       * @range:
       * @mandatory: true
       * @default:
       */
	public final static String dbname = "dbname";
	/*
       * @name: username
       * @description:  Oracle登录用户
       * @range:
       * @mandatory: true
       * @default:
       */
	public final static String username = "username";
	/*
       * @name: password
       * @description: Oracle登录密码
       * @range:
       * @mandatory: true
       * @default:
       */
	public final static String password = "password";
	/*
       * @name: schema
       * @description: Oracle数据库schema
       * @range:
       * @mandatory: true
       * @default:
       */
	public final static String schema = "schema";
	 /*
       * @name: ip
       * @description: Oracle IP地址
       * @range:
       * @mandatory: true
       * @default:
       */
	public final static String ip = "ip";
	/*
       * @name: port
       * @description: Oracle端口
       * @range:
       * @mandatory: true
       * @default: 1521
       */
	public final static String port = "port";
	/*
       * @name: tables
       * @description: 表名
       * @range: 
       * @mandatory: true
       * @default: 
       */
	public final static String tables = "tables";
	
	/*
       * @name: columns
       * @description: 列名
       * @range: 
       * @mandatory: false
       * @default: *
       */
	public final static String columns = "columns";
	
	/*
       * @name: where
       * @description: where条件
       * @range: 
       * @mandatory: false
       * @default: 
       */
	public final static String where = "where";		
	/*
       * @name: sql
       * @description: 自定义sql
       * @range: 
       * @mandatory: false
       * @default: 
       */
	public final static String sql = "sql";
	/*
       * @name: encoding
       * @description: 数据库编码
       * @range: UTF-8|GBK|GB2312
       * @mandatory: false
       * @default: UTF-8
       */
	public final static String encoding = "encoding";
	/*
       * @name: split_mod
       * @description: 任务切分模式
       * @range: 0-no split, 1-rowid split, others ntile split 
       * @mandatory: false
       * @default: 1
       */
	public final static String splitMod = "split_mod";
	/*
       * @name: tnsfile
       * @description: tns文件路径
       * @range: 
       * @mandatory: true
       * @default: /home/oracle/product/10g/db/network/admin/tnsnames.ora
       */
	public final static String tnsFile = "tnsfile";
	
	/*
     * @name: schema
     * @description: Oracle数据库schema,比如：
     * (DESCRIPTION =
		    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.223.46)(PORT = 1521))
		    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.223.47)(PORT = 1521))
		    (LOAD_BALANCE = yes)
		    (CONNECT_DATA =
		      (SERVER = DEDICATED)
		      (SERVICE_NAME = busdb)
		    )
		  )
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String racUrl="racurl";

	 /*
       * @name:concurrency
       * @description:任务的并发线程数
       * @range:1-100
       * @mandatory: false
       * @default:1
       */
	public final static String concurrency = "concurrency";
}
