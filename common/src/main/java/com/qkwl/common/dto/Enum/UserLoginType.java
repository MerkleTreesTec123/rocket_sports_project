package com.qkwl.common.dto.Enum;

public enum UserLoginType{
	
	WebUser(1, "WEB用户"), 
	APPUser(2, "APP用户"), 
	API(3, "API接口"),
	WebQQ(4, "WEB-QQ"),
	APPQQ(5, "APP-QQ"),
	WebWX(6, "WEB-WX"),
	APPWX(7, "APP-WX");

	private Integer code;
	private Object value;

	private UserLoginType(Integer code, Object value) {
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
