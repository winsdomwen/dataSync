package com.gci.datax.plugins.writer.streamwriter;

public final class ParamKey {
	
	/*
	 * @name: fieldSplit
	 * @description: seperator to seperate field
	 * @range:
	 * @mandatory: false 
	 * @default:\t
	 */
	public final static String fieldSplit = "field_split";
	
	/*
	 * @name: encoding
	 * @description: stream encode
	 * @range: UTF-8|GBK|GB2312
	 * @mandatory: false
	 * @default: UTF-8
	 */
	public final static String encoding = "encoding";
	/*
	 * @name: prefix 
	 * @description:  print result with prefix
	 * @range: 
	 * @mandatory: false
	 * @default:
	 */
	public final static String prefix = "prefix";
	
	/*
	 * @name: print
	 * @description: print the result
	 * @range: 
	 * @mandatory: false
	 * @default: true
	 */
	public final static String print = "print";
	
	/*
	 * @name: nullchar
	 * @description:  replace null with the nullchar
	 * @range: 
	 * @mandatory: false
	 * @default: 
	 */
	public final static String nullChar = "nullchar";

	 /*
       * @name:concurrency
       * @description:concurrency of the job
       * @range:1
       * @mandatory: false
       * @default:1
       */
	public final static String concurrency = "concurrency";
}

