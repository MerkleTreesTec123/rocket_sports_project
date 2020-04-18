package com.qkwl.common.dto.Enum;

public enum UserBindTypeEnum {
	
	PHONE(1, "手机绑定"), EMAIL(2, "邮箱绑定"), GOOGLE(3, "谷歌绑定");
	
	private Integer code;
	private Object value;

	private UserBindTypeEnum(Integer code, Object value) {
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
