package com.qkwl.common.redis;

import java.io.Serializable;

import com.qkwl.common.util.Utils;

/**
 * Redis传输对象封装
 * @author ZKF
 */
public class RedisObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 认证信息类型
	 */
	private RedisAuthTypeEnum redisAuthTypeEnum;

	/**
	 * 最近活动时间
	 */
	private long lastActiveDateTime = Utils.getTimestamp().getTime() / 1000;

	/**
	 * 扩展对象
	 */
	private Object extObject;

	public RedisAuthTypeEnum getRedisAuthTypeEnum() {
		return redisAuthTypeEnum;
	}

	public void setRedisAuthTypeEnum(RedisAuthTypeEnum redisAuthTypeEnum) {
		this.redisAuthTypeEnum = redisAuthTypeEnum;
	}

	public long getLastActiveDateTime() {
		return lastActiveDateTime;
	}

	public void setLastActiveDateTime(long lastActiveDateTime) {
		this.lastActiveDateTime = lastActiveDateTime;
	}

	public Object getExtObject() {
		return extObject;
	}

	public void setExtObject(Object extObject) {
		this.extObject = extObject;
	}
}
