package com.qkwl.common.rpc.redis;

/**
 * redis接口
 * @author TT
 */
public interface IRedisService {

	/**
	 * 重置redis数据
	 */
	public void resetRedis(int type);

	/**
	 * 清空redis数据
	 */
	public void clearRedis(int type);
}
