package com.qkwl.common.redis;

/**
 * Redis认证枚举
 * @author ZKF 
 * 
 */
public enum RedisAuthTypeEnum {
	ConsoleAdminUser(0, "管理员用户"), PCUser(1, "PC用户"), APPUser(2, "APP用户");

	private Integer code;
	private Object value;

	private RedisAuthTypeEnum(Integer code, Object value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
