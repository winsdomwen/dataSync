package com.gci.datax.plugins.writer.mysqlwriter;

public final class ParamKey {
	 /*
      * @name: ip
      * @description: Mysql database ip address
      * @range:
      * @mandatory: true
      * @default:
      */
	public final static String ip = "ip";
	/*
      * @name: port
      * @description: Mysql database port
      * @range:
      * @mandatory: true
      * @default:3306
      */
	public final static String port = "port";
	/*
      * @name: dbname
      * @description: Mysql database name
      * @range:
      * @mandatory: true
      * @default:
      */
	public final static String dbname = "dbname";
	/*
      * @name: username
      * @description: Mysql database login username
      * @range:
      * @mandatory: true
      * @default:
      */
	public final static String username = "username";
	/*
      * @name: password
      * @description: Mysql database login password
      * @range:
      * @mandatory: true
      * @default:
      */
	public final static String password = "password";
	/*
      * @name: table
      * @description: table to be dumped data into
      * @range: 
      * @mandatory: true
      * @default: 
      */
	public final static String table = "table";
	/*
      * @name: colorder
      * @description: order of columns
      * @range: 
      * @mandatory: false
      * @default:
      */
	public final static String colorder = "colorder";
	/*
      * @name: encoding
      * @description: 
      * @range: UTF-8|GBK|GB2312
      * @mandatory: false
      * @default: UTF-8
      */
	public final static String encoding = "encoding";
	/*
	 * @name: pre
	 * @description: execute sql before dumping data
	 * @range:
	 * @mandatory: false
	 * @default:
	 */
	public final static String pre = "pre";
	/*
	 * @name: post
	 * @description: execute sql after dumping data
	 * @range:
	 * @mandatory: false
	 * @default:
	 */
	public final static String post = "post";

	/*
	 * @name: limit
	 * @description: error limit
	 * @range: [0-65535]
	 * @mandatory: false
	 * @default: 0
	 */
	public final static String limit = "limit";
	/*
	 * @name: set
	 * @description:
	 * @range:
	 * @mandatory: false
	 * @default:
	 */
	public final static String set = "set";
	/*
	 * @name: replace
	 * @description:
	 * @range: [true/false]
	 * @mandatory: false
	 * @default:false
	 */
	public final static String replace = "replace";
     /*
      * @name:mysql.params
      * @description:mysql driver params
      * @range:params1|params2|...
      * @mandatory: false
      * @default:
      */
	public final static String mysqlParams = "mysql.params";

	 /*
      * @name:concurrency
      * @description:concurrency of the job
      * @range:1-100
      * @mandatory: false
      * @default:1
      */
	public final static String concurrency = "concurrency";

}

