package com.gci.datax.plugins.writer.rediswriter;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.plugin.PluginStatus;
import com.gci.datax.common.plugin.Writer;

public class RedisWriter extends Writer {

	private Logger logger = Logger.getLogger(RedisWriter.class);
	private String ip;
	private int port;
	private int maxactive;
	private int maxidle;
	private long maxwait;
	private String keys;
	private int failCount;// count error lines

	@Override
	public int init() {
		ip = param.getValue(ParamKey.ip, "");
		port = param.getIntValue(ParamKey.port, 6379);
		maxactive = param.getIntValue(ParamKey.maxactive, -1);
		maxidle = param.getIntValue(ParamKey.maxidle, 5);
		maxwait = param.getIntValue(ParamKey.maxwait, 1000 * 100);
		keys = param.getValue(ParamKey.keys, "");

		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int connect() {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int startWrite(LineReceiver receiver) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxActive(this.maxactive);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(this.maxidle);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWait(this.maxwait);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			pool = new JedisPool(config, this.ip, this.port);
			jedis = pool.getResource();

			/* load data begin */
			Line line = null;
			int lines = 0;
			String[] keyArr = keys.split(",");
			while ((line = receiver.getFromReader()) != null) {
				try {
					for (int i = 0; i < line.getFieldNum(); i++) {
						// ps.setObject(i + 1, line.getField(i));
						jedis.set(keyArr[i], line.getField(i));
					}
				} catch (Exception e) {
					failCount++;
					logger.debug("失败行(" + e.getMessage() + "):" + line);
				}
			}

			if (failCount > 1)
				logger.debug("总共插入错误行数:" + failCount);
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				// 释放对象池 
				if(jedis != null) {
					pool.returnResource(jedis);
				}
			} catch (Exception e) {
			}
		}
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int commit() {
		return PluginStatus.SUCCESS.value();
	}

	@Override
	public int finish() {
		return PluginStatus.SUCCESS.value();
	}

}
