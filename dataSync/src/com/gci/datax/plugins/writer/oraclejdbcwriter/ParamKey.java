package com.gci.datax.plugins.writer.oraclejdbcwriter;

public final class ParamKey {

	 /*
     * @name: ip
     * @description: ip
     * @range:
     * @mandatory: true
     * @default:
     */
	public final static String ip = "ip";
	/*
     * @name: port
     * @description: 端口
     * @range:
     * @mandatory: true
     * @default:3306
     */
	public final static String port = "port";
	
	/*
	 * @name: dbname
	 * @description: Oracle数据库名
	 * @range:
	 * @mandatory: true
	 * @default:
	 */
	public final static String dbname = "dbname";
	
	/*
     * @name: racurl
     * @description: oracle集群RAC的url地址
     * @range: 
     * @mandatory: true
     * @default: 
     */
	public final static String racUrl="racurl";
	
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
	public final static String schema = "schema";
	
	/*
	 * @name: table
	 * @description: 存储数据的表名
	 * @range:
	 * @mandatory: true
	 * @default:
	 */
	public final static String table = "table";
    /*
	 * @name: username
	 * @description: oracle登录用户
	 * @range:
	 * @mandatory: true
	 * @default:
	 */
	public final static String username = "username";
	
	/*
	 * @name: password
	 * @description: oracle登录密码
	 * @range:
	 * @mandatory: true
	 * @default:
	 */
	public final static String password = "password";

	/*
	 * @name: pre
	 * @description: 写入数据前先执行的sql语句
	 * @range:
	 * @mandatory: true
	 * @default:
	 */
	public final static String pre = "pre";
	/*
	 * @name: post
	 * @description: 写入数据后执行的sql语句
	 * @range:
	 * @mandatory: false
	 * @default:
	 */
	public final static String post = "post";
	
	/*
	 * @name: insert
	 * @description: 自定义insert sql
	 * @range:
	 * @mandatory: false
	 * @default:
	 */
	public final static String insert = "insert";
	
	/*
	 * @name: encoding
	 * @description: 编码
	 * @range: UTF-8|GBK|GB2312
	 * @mandatory: false
	 * @default: UTF-8
	 */
	public final static String encoding = "encoding";
	
	/*
	 * @name: limit
	 * @description: 最大错误数
	 * @range:
	 * @mandatory: false
	 * @default: 1000
	 */
	public final static String limit = "limit";
	
	/*
	 * @name: dtfmt
	 * @description: 日期格式化
	 * @range:
	 * @mandatory: false
	 * @default: yyyy-mm-dd hh24:mi:ss
	 */
	public final static String dtfmt = "dtfmt";
	
	/*
	 * @name: colorder
	 * @description: 对应的插入列
	 * @range:col1,col2
	 * @mandatory: false
	 * @default: 
	 */
	public final static String colorder = "colorder";
	
	 /*
       * @name:concurrency
       * @description:任务并发线程数
       * @range:1-100
       * @mandatory: false
       * @default:1
       */
	public final static String concurrency = "concurrency";
	
	/*
	 * @name:commitCount
	 * @description:每次commit的行数
	 * @default: 50000
	 */
	public final static String commitCount = "commitCount";
	
	/*
	 * @name:duplicatedThreshold
	 * @description:重复记录处理数目 
	 * @default: 10000
	 */
	public final static String duplicatedThreshold = "duplicatedThreshold";
	
	/*
	 * @name:onDuplicatedSql
	 * @description:处理重复记录的sql
	 * @default: delete from TB_NAME_HERE where KEY_COL_NAME_HERE=?
	 */
	public final static String onDuplicatedSql = "onDuplicatedSql";
	
	/*
	 * @name:duplidatedKeyIndex
	 * @description:重复记录的key,以逗号隔开
	 * @default: 0,
	 */
	public final static String duplicatedKeyIndices = "duplicatedKeyIndices";
}
