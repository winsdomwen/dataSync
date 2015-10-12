package com.gci.datax.engine.schedule;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gci.datax.common.plugin.PluginParam;

/**
 * 对ThreadPoolExecutor的一个简单包装类.
 * 
 * */
public class NamedThreadPoolExecutor extends ThreadPoolExecutor {
	String name;

	PluginParam param;

	PluginWorker postWorker;

	/**
	 * 构造函数
	 * 
	 * @param	name
	 * 			线程池名称
	 * 
	 * @param	corePoolSize
	 * 			主线程数
	 * 
	 * @param	maximumPoolSize
	 * 			最大线程数
	 * 
	 * @param	keepAliveTime
	 *  		when the number of threads is greater than </br>
     * 			the core, this is the maximum time that excess idle threads </br>
     * 			will wait for new tasks before terminating. </br>
     * 
     * @param	unit
     * 			keepAliveTim的时间单位
     * 
     * @param	workQueue
     * 			workQueue the queue to use for holding tasks before they </br>
     *			are executed. This queue will hold only the <tt>Runnable</tt> </br>
     * 			tasks submitted by the <tt>execute</tt> method. <br>
	 * */
	NamedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void doPrepare() {
		postWorker.prepare(param);
	}

	public void doPost() {
		postWorker.post(param);
	}
	
	public void doCleanup() {
		postWorker.cleanup();
	}

	public PluginParam getParam() {
		return param;
	}

	public void setParam(PluginParam param) {
		this.param = param;
	}

	public PluginWorker getPostWorker() {
		return this.postWorker;
	}

	public void setPostWorker(PluginWorker workerInPool) {
		this.postWorker = workerInPool;
	}

}
