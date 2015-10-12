package com.gci.datax.plugins.reader.mysqlreader;

public final class ParamKey {
		 /*
	       * @name: ip
	       * @description: Mysql数据库IP
	       * @range:
	       * @mandatory: true
	       * @default:
	       */
		public final static String ip = "ip";
		/*
	       * @name: port
	       * @description: Mysql数据库端口
	       * @range:
	       * @mandatory: true
	       * @default:3306
	       */
		public final static String port = "port";
		/*
	       * @name: dbname
	       * @description: Mysql数据库名称
	       * @range:
	       * @mandatory: true
	       * @default:
	       */
		public final static String dbname = "dbname";
		/*
	       * @name: username
	       * @description: Mysql数据库登录用户
	       * @range:
	       * @mandatory: true
	       * @default:
	       */
		public final static String username = "username";
		/*
	       * @name: password
	       * @description: Mysql登录密码
	       * @range:
	       * @mandatory: true
	       * @default:
	       */
		public final static String password = "password";
		/*
	       * @name: tables
	       * @description: 读取数据的表名,支持简单的regex, table[0-63]
	       * @range: 
	       * @mandatory: true
	       * @default: 
	       */
		public final static String tables = "tables";
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
	       * @name: columns
	       * @description: 查询的字段, 默认是*
	       * @range: 
	       * @mandatory: false
	       * @default: *
	       */
		public final static String columns = "columns";
		/*
	       * @name: encoding
	       * @description: 编码
	       * @range: UTF-8|GBK|GB2312
	       * @mandatory: false
	       * @default: UTF-8
	       */
		public final static String encoding = "encoding";
		
       /*
	       * @name: mysql.params
	       * @description: mysql驱动参数, 比如： loginTimeOut=3000&yearIsDateType=false
	       * @range: 
	       * @mandatory: false
	       * @default:
	       */
		public final static String mysqlParams = "mysql.params";
		
		 /*
	       * @name: concurrency
	       * @description: 并发线程数
	       * @range: 1-10
	       * @mandatory: false
	       * @default: 1
	       */
		public final static String concurrency = "concurrency";
}
