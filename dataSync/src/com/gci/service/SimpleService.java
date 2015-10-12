package com.gci.service;

import java.io.Serializable;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gci.Constant;
import com.gci.datax.common.constants.Constants;
import com.gci.datax.engine.schedule.Engine;

@Service("simpleService")
public class SimpleService implements Serializable {
	private static final long serialVersionUID = 8819476406299052905L;
	private static final Logger logger = Logger.getLogger(SimpleService.class);

	public void testMethod(String triggerName, String group) {
			// 这里执行定时调度业务
			logger.info("开始执行调度任务:" + triggerName + "==" + group + " num: "
					+ Constant.jobList.size());
			// Engine.dataChange(Constants.DATAX_LOCATION+"/jobs/"+triggerName+".xml");
			//String clsPath = this.getClass().getClassLoader().getResource("").getPath();
			String clsPath = Constants.DATAX_LOCATION;
			//clsPath = clsPath.charAt(0)=='/'?clsPath.substring(1):clsPath;
			//logger.info(clsPath.substring(0, clsPath.indexOf("WEB-INF"))+ "jobs/" + triggerName + ".xml");
			logger.info(clsPath+ "/jobs/" + triggerName + ".xml");			
			//Engine.dataChange(triggerName,clsPath.substring(0, clsPath.indexOf("WEB-INF"))+ "jobs/" + triggerName + ".xml");			
			Engine.dataChange(triggerName,clsPath+ "/jobs/" + triggerName + ".xml");
	}

	public void testMethod2(String triggerName, String group) {
		// 这里执行定时调度业务
		logger.info("BBBB:" + triggerName + "==" + group);
	}

}
