package com.gci.datax.engine.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gci.datax.common.exception.DataExchangeException;
import com.gci.datax.common.plugin.Writer;
import com.gci.datax.engine.conf.EngineConf;
import com.gci.datax.engine.conf.JobConf;
import com.gci.datax.engine.conf.JobPluginConf;
import com.gci.datax.engine.schedule.Engine;

/**
 * 缓冲区池
 * 
 * */
public class StoragePool {

	private Map<String, Storage> storageMap = new HashMap<String, Storage>();

	/**
	 * 构造函数
	 * 为每个{@link Writer}生成一个{@link Storage}.
	 * 
	 * @param	jobConf	
	 * 			任务配置
	 * 
	 * @param 	engineConf	
	 * 			{@link Engine}配置
	 * 
	 * @param 	period
	 * 			{@link Storage}统计报告的时间周期
	 * 
	 * */
	public StoragePool(JobConf jobConf, EngineConf engineConf, int period) {
		
		String storageClassName = engineConf.getStorageClassName();
        int lineLimit = engineConf.getStorageLineLimit();
		int byteLimit = engineConf.getStorageByteLimit();
		
		for (JobPluginConf jpc : jobConf.getWriterConfs()) {
            String cStorageClassName = storageClassName;
            int cLineLimit = lineLimit;
            int cByteLimit = byteLimit;
            int destructLimit = jpc.getDestructLimit();
            
            try {
                cStorageClassName = jpc.getPluginParams().getValue("storageClassName", storageClassName);
                cLineLimit = jpc.getPluginParams().getIntValue("lineLimit", lineLimit);
                cByteLimit = jpc.getPluginParams().getIntValue("byteLimit", byteLimit);
                
            } catch (Exception e) {
                throw new DataExchangeException(e.getCause());
            }
            
			Storage s = StorageFactory.product(cStorageClassName);
			s.init(jpc.getId(), cLineLimit, cByteLimit, destructLimit);
			s.getStat().setPeriodInSeconds(period);
			storageMap.put(jpc.getId(), s);
		}
	}

	public List<Storage> getStorageForReader() {
		List<Storage> ret = new ArrayList<Storage>();
		for (Storage s : storageMap.values()) {
			ret.add(s);
		}
		return ret;
	}


	public Storage getStorageForWriter(String id) {
		return storageMap.get(id);
	}

	/**
	 * 关闭从数据源加载数据到存储的通道
	 * 
	 */
	public void closeInput() {
		for (String k : storageMap.keySet()) {
			storageMap.get(k).setPushClosed(true);
		}
	}

	/**
	 * 获取周期状态
	 */
	public String getPeriodState() {
		StringBuilder sb = new StringBuilder(100);
		for (String k : storageMap.keySet()) {
			sb.append(storageMap.get(k).getStat().getPeriodState());
			storageMap.get(k).getStat().periodPass();
		}
		return sb.toString();
	}

	public String getTotalStat() {
		StringBuilder sb = new StringBuilder(100);
		for (String k : storageMap.keySet()) {
			sb.append(storageMap.get(k).getStat().getTotalStat());
		}
		return sb.toString();
	}

}
