package com.gci.datax.plugins.reader.streamreader;

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
	 * @description: environment encode 
	 * @range: UTF-8|GBK|GB2312
	 * @mandatory: false
	 * @default: UTF-8
	 */
	public final static String encoding = "encoding";
	
	/*
       * @name: nullString
       * @description: replace nullString to null
       * @range: 
       * @mandatory: false
       * @default: \N
       */
	public final static String nullString = "null_string";

	 /*
       * @name:concurrency
       * @description:concurrency of the job
       * @range:1-100
       * @mandatory: false
       * @default:1
       */
	public final static String concurrency = "concurrency";
}
