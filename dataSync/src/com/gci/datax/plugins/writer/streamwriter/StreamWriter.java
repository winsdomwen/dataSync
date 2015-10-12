/**
 * (C) 2010-2011 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 
 * version 2 as published by the Free Software Foundation. 
 * 
 */


package com.gci.datax.plugins.writer.streamwriter;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.exception.ExceptionTracker;
import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Writer;


public class StreamWriter extends Writer {
	private char FIELD_SPLIT = '\t';

	private String ENCODING = "UTF-8";

	private String PREFIX = "";
	
	private boolean printable = true;
	
	private String nullString = "";

	private Logger logger = Logger.getLogger(StreamWriter.class
			.getCanonicalName());
	
	@Override
	public int init() {
		this.FIELD_SPLIT = param.getCharValue(
				ParamKey.fieldSplit, '\t');
		this.ENCODING = param
				.getValue(ParamKey.encoding, "UTF-8");
		this.PREFIX = param.getValue(ParamKey.prefix, "");
		this.nullString = param.getValue(ParamKey.nullChar,
				this.nullString);
		this.printable = param.getBoolValue(ParamKey.print,
				this.printable);

		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int connect() {
		return PluginStatus.SUCCESS.value();
	}

	private String makeVisual(Line line) {
		if (line == null || line.getFieldNum() == 0) {
			return this.PREFIX + "\n";
		}

		int i = 0;
		String item = null;
		int num = line.getFieldNum();
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.PREFIX);
		for (i = 0; i < num; i++) {
			item = line.getField(i);
			if (null == item) {
				sb.append(nullString);
			} else {
				sb.append(item);
			}
			
			if (i != num - 1) {
				sb.append(FIELD_SPLIT);
			} else {
				sb.append('\n');
			}
		}
		
		return sb.toString();
	}

	@Override
	public int startWrite(LineReceiver linereceiver){
		Line line;
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					System.out, this.ENCODING));
			while ((line = linereceiver.getFromReader()) != null) {
				if (this.printable) {
					writer.write(makeVisual(line));
				} else {
					/* do nothing */
				}
			}
			writer.flush();
			return PluginStatus.SUCCESS.value();
		}  catch (Exception e) {
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}

	@Override
	public int commit() {
		return 0;
	}

	@Override
	public int finish() {
		return 0;
	}

}
