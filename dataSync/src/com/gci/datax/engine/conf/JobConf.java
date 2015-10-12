package com.gci.datax.engine.conf;

import java.util.List;

/**
 * 任务配置.
 * 
 * @see Reader
 * @see Writer
 * 
 * */
public class JobConf {

	private String id;

	private JobPluginConf readerConf;

	private List<JobPluginConf> writerConfs;

	public JobPluginConf getReaderConf() {
		return readerConf;
	}

	public void setReaderConf(JobPluginConf readerConf) {
		this.readerConf = readerConf;
	}

	public List<JobPluginConf> getWriterConfs() {
		return this.writerConfs;
	}

	public void setWriterConfs(List<JobPluginConf> writerConfs) {
		this.writerConfs = writerConfs;
	}

	public int getWriterNum() {
		return this.writerConfs.size();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public boolean validate() {
		if (id.length() == 0) {
			return false;
		}
		if (!this.readerConf.validate()) {
			return false;
		}
		for (JobPluginConf dpc : this.writerConfs) {
			if (!dpc.validate()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(300);
		sb.append(String.format("\njob:%s", this.getId()));
		sb.append("\nReader配置:");
		sb.append(this.readerConf.toString());
		sb.append(String.format("\n\nWriter配置 [num %d]:", this.writerConfs.size()));
		for (JobPluginConf dpc : this.writerConfs) {
			sb.append(dpc.toString());
		}
		return sb.toString();
	}

}
