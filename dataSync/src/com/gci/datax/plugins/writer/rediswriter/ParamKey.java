package com.gci.datax.plugins.writer.rediswriter;

public final class ParamKey {
	
	public final static String ip = "ip";

	public final static String port = "port";
	
	/***
	 * //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	 */
	public final static String maxactive="maxactive";
	
	/***
	 * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例
	 */
	public final static String maxidle="maxidle";
	
	/***
	 * 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
	 */
	public final static String maxwait="maxwait";
	
	public final static String keys="keys";
	
}
