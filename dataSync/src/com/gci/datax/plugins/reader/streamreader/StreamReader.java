/**
 * (C) 2010-2011 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 
 * version 2 as published by the Free Software Foundation. 
 * 
 */


package com.gci.datax.plugins.reader.streamreader;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineSender;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Reader;



/**
 * @author bazhen.csy
 *
 */
public class StreamReader extends Reader {	
	private char FIELD_SPLIT = '\t';
	
	private String ENCODING = "UTF-8";
	
	private String nullString = "";
	
	private static Logger logger = Logger.getLogger(StreamReader.class.getCanonicalName());
	
	@Override
	public int init() {
		this.FIELD_SPLIT = param.getCharValue(ParamKey.fieldSplit, '\t');
		this.ENCODING = param.getValue(ParamKey.encoding, "UTF-8");
		this.nullString = param.getValue(ParamKey.nullString, "");
		
		return PluginStatus.SUCCESS.value();
	}


	@Override
	public int connect() {
		return 0;
	}

	private String changeNull(final String item) {
		if (nullString != null && nullString.equals(item)) {
			return null;
		}
		return item;
	}

	@Override
	public int startRead(LineSender resultWriter){
		int ret = PluginStatus.SUCCESS.value();
		
		int previous;
		String fetch;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in, this.ENCODING));
			while ((fetch = reader.readLine()) != null) {
				previous = 0;
				Line line = resultWriter.createLine();
				for (int i = 0; i < fetch.length(); i++) {
					if (fetch.charAt(i) == this.FIELD_SPLIT) {
						line.addField(changeNull(fetch.substring(previous, i)));
						previous = i + 1;
					}
				}
				line.addField(fetch.substring(previous));
				resultWriter.sendToWriter(line);
			}
			resultWriter.flush();
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
		
		return ret;
	}


	@Override
	public int finish(){
		return PluginStatus.SUCCESS.value();
	}
}
