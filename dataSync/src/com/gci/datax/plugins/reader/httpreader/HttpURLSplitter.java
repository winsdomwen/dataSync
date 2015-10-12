/**
 * (C) 2010-2011 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 
 * version 2 as published by the Free Software Foundation. 
 * 
 */


package com.gci.datax.plugins.reader.httpreader;

import java.util.ArrayList;
import java.util.List;

import com.gci.datax.common.plugin.PluginParam;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Splitter;
import com.gci.datax.common.util.SplitUtils;


public class HttpURLSplitter extends Splitter {

	private String URLDelimiter = ";";
	private String httpURLs = "";

	@Override
	public int init(){
		this.URLDelimiter = param.getValue(
				ParamKey.URLDelimiter, ";");
		this.httpURLs = param.getValue(ParamKey.httpURLs);
		
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public List<PluginParam> split(){

		List<PluginParam> v = new ArrayList<PluginParam>();
		String[] urls = httpURLs.split(this.URLDelimiter);
		for (String url : urls) {
			if ("" != url) {
				PluginParam oParams = SplitUtils.copyParam(this
						.getParam());
				oParams.putValue(ParamKey.httpURLs, url.trim());
				v.add(oParams);
			}
		}
		return v;
	}

}
