package com.qkwl.common.dto.Enum;

public enum ApiLimitTypeEnum {
	
	APITrade(1, "API交易");

	private int code;
	private String value;

	private ApiLimitTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
