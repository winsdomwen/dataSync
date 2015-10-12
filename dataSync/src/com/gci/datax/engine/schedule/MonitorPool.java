package com.gci.datax.engine.schedule;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.plugin.PluginMonitor;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.engine.plugin.DefaultPluginMonitor;
import com.gci.datax.engine.storage.Storage;

import org.apache.log4j.Logger;

import javax.management.monitor.Monitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控池,
 * 
 * */
public class MonitorPool {
	private static final Logger logger = Logger.getLogger(MonitorPool.class);

	private List<PluginMonitor> monitors;


	public MonitorPool() {
		monitors = new ArrayList<PluginMonitor>();
	}

	/**
	 * 增加一个{@link PluginWorker}到监控池。
	 * 
	 * @param	worker
	 * 			{@link PluginWorker}
	 * 
	 * @throws	DataExchangeException
	 * 
	 * */
	public void monitor(PluginWorker worker) {
		PluginMonitor monitor = new DefaultPluginMonitor();
		monitor.setTargetName(worker.getPluginName());
		monitor.setTargetId(worker.getMyIndex());
		worker.setMonitor(monitor);
		monitors.add(monitor);
	}

	/**
	 * 打印{@link Storage}的统计.
	 * 
	 * */
	public void stat() {
		long successLine = 0;
		long failedLine = 0;
		Map<String, Integer> statusCnt = new HashMap<String, Integer>();
		for (PluginMonitor m : monitors) {
			successLine += m.getSuccessedLines();
			failedLine += m.getFailedLines();
			Integer cnt = statusCnt.get(m.getStatus().toString());
			if (cnt != null)
				cnt += 1;
			else
				statusCnt.put(m.getStatus().toString(), 1);
		}
		logger.info(String.format("成功行数  %d, 失败行数 %d",
				successLine, failedLine));
		for (PluginStatus m : PluginStatus.values()) {
			if (statusCnt.get(m.toString()) != null)
				logger.info(String.format("Status %s Cnt %d", m.toString(),
						statusCnt.get(m.toString())));
		}
	}

	/**
	 * 获取丢弃的记录数
	 * 
	 * @return		
	 * 
	 * */
	public long getDiscardLine() {
		long discardLine = 0;
		for (PluginMonitor m : monitors) {
			discardLine += m.getFailedLines();
		}
		return discardLine;
	}
}
