package com.qkwl.common.dto.Enum;

public enum MatchTypeEnum {
	LIMITE(0, "限价单"),		// 限价单
	MARKET(1, "市价单"),		// 市价单
	HUOBI(2, "火币单"),		// 火币单
	CHBTC(3, "CHBTC单");		// CHBTC单
	
	private Integer code;
	private String value;
	
	private MatchTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
