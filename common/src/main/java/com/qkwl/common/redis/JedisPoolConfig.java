package com.qkwl.common.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisPoolConfig extends GenericObjectPoolConfig {

	public JedisPoolConfig() {
		// 最大连接数
		setMaxTotal(100);
		// 最大空闲连接数
		setMaxIdle(10);
		// 最小空闲连接数
		setMinIdle(10);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		setMaxWaitMillis(60000);
		// 在空闲时检查有效性
		setTestWhileIdle(true);
		// 在获取连接的时候检查有效性, 默认false
		setTestOnBorrow(true);
		setTestOnReturn(true);
		// 逐出连接的最小空闲时间
		setMinEvictableIdleTimeMillis(60000);
		// 逐出扫描的时间间隔
		setTimeBetweenEvictionRunsMillis(30000);
		// 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		setNumTestsPerEvictionRun(-1);
	}
}