/**
 * (C) 2010-2011 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 
 * version 2 as published by the Free Software Foundation. 
 * 
 */


package com.gci.datax.plugins.reader.httpreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.log4j.Logger;

import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Reader;



public class HttpReader extends Reader {

	private static Logger logger = Logger.getLogger(HttpReader.class
			.getCanonicalName());

	private String FIELD_SPLIT = "\t";
	private String ENCODING = "UTF-8";
	private String nullString = "";
	private String httpURL = "";

	@Override
	public int init(){
		
		logger.info("begin init httpreader");

		this.FIELD_SPLIT = param.getValue(ParamKey.fieldSplit,
				"\t");
		this.ENCODING = param.getValue(ParamKey.encoding,
				"UTF-8");
		this.nullString = param.getValue(ParamKey.nullString,
				"");
		this.httpURL = param.getValue(ParamKey.httpURLs);
		
		logger.info("end init httpreader");
		return 0;
	}

	@Override
	public int connect() {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int prepare(PluginParam param) {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split(PluginParam param) {
		HttpURLSplitter spliter = new HttpURLSplitter();
		spliter.setParam(param);
		spliter.init();
		return spliter.split();
	}

	@Override
	public int startRead(LineSender sender) {
		logger.info("begin startLoad httpreader");
		int ret = PluginStatus.SUCCESS.value();

		try {
			URL url = new URL(this.httpURL);
			URLConnection urlc = null;
			try {
				urlc = url.openConnection();
				HttpURLConnection httpUrlConnection = (HttpURLConnection) urlc;
				BufferedReader in = new BufferedReader(new InputStreamReader(
						httpUrlConnection.getInputStream(),this.ENCODING));
				String readline = in.readLine();
				while (null != readline) {
					Line line = sender.createLine();
					String[] strs = readline.split(this.FIELD_SPLIT);
					for (String str : strs) {
						if (!str.equals(this.nullString)) {
							line.addField(str);
						} else {
							line.addField(null);
						}
					}
					sender.sendToWriter(line);
					readline = in.readLine();
				}
				sender.flush();
			} catch (IOException e) {
				logger.error(ExceptionTracker.trace(e));
				String message = String.format("Httpreader failed: %s,%s",
						e.getMessage(), e.getCause());
				throw new DataExchangeException(message);
			}
		} catch (MalformedURLException e) {
			logger.error(ExceptionTracker.trace(e));
			String message = String.format("Httpreader failed: %s,%s",
					e.getMessage(), e.getCause());
			throw new DataExchangeException(message);
		}

		logger.info("HttpReader read work ends .");
		return ret;
	}

	@Override
	public int finish() {
		return PluginStatus.SUCCESS.value();
	}

}
