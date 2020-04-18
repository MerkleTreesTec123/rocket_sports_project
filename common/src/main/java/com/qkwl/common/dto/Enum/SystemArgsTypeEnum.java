package com.qkwl.common.dto.Enum;

public enum SystemArgsTypeEnum {
	ADMIN(1, "网站后台"),		// 网站后台
	FRONT(2, "网站前台");		// 网站前台
	
	private Integer code;
	private Object value;

	private SystemArgsTypeEnum(Integer code, Object value) {
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
	
	public static String getValueByCode(Integer code) {
		for (SystemArgsTypeEnum systemArgsTypeEnum : SystemArgsTypeEnum.values()) {
			if (systemArgsTypeEnum.getCode().equals(code)) {
				return systemArgsTypeEnum.getValue().toString();
			}
		}
		return null;
	}
}
