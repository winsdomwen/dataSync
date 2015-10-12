package com.gci.datax.engine.plugin;

import org.apache.log4j.Logger;

import com.gci.datax.common.plugin.PluginMonitor;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Pluginable;


/**
 * {@link PluginMonitor}的实现类, 监控{@link Pluginable}状态, 
 * 
 * */
public class DefaultPluginMonitor implements PluginMonitor {

	private Logger logger = Logger.getLogger(DefaultPluginMonitor.class);

	private long successedLines;

	private long failedLines;

	private PluginStatus status;

	private String targetName;

	private int targetId;

	public DefaultPluginMonitor() {
		successedLines = 0;
		failedLines = 0;
		targetId = 0;
		this.status = PluginStatus.WAITING;
	}

	@Override
	public String getTargetName() {
		return targetName;
	}

	@Override
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public int getTargetId() {
		return targetId;
	}

	@Override
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	@Override
	public PluginStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(PluginStatus status) {
		this.status = status;
	}

	@Override
	public long getFailedLines() {
		return failedLines;
	}

	@Override
	public long getSuccessedLines() {
		return successedLines;
	}

	@Override
	public int lineFail(String info) {
		failedLines++;
		return 0;
	}

	@Override
	public int lineSuccess() {
		successedLines++;
		return 0;
	}

	@Override
	public int setFailedLines(long i) {
		failedLines = i;
		return 0;
	}

	@Override
	public int setSuccessedLines(long i) {
		successedLines = i;
		return 0;
	}

}
