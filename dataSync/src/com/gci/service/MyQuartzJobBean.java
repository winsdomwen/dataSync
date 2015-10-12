package com.gci.service;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MyQuartzJobBean extends QuartzJobBean {

	private SimpleService simpleService;

	public void setSimpleService(SimpleService simpleService) {
		this.simpleService = simpleService;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {
		Trigger trigger = jobexecutioncontext.getTrigger();
		String triggerName = trigger.getName();
		String group = trigger.getGroup();
		
		//根据Trigger组别调用不同的业务逻辑方法
		if (StringUtils.equals(group, Scheduler.DEFAULT_GROUP)) {
			simpleService.testMethod(triggerName, group);
		} else {
			simpleService.testMethod2(triggerName, group);
		}
	}

}
