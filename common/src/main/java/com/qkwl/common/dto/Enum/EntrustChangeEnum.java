package com.qkwl.common.dto.Enum;

public enum EntrustChangeEnum {
	BUY(0, "买单"),		// 买单
	SELL(1, "卖单"),		// 卖单
	SUCCEED(2, "成交"),	// 成交
	CANCEL(3, "撤销");	// 撤销

	private Integer code;
	private String value;
	
	EntrustChangeEnum(int code, String value) {
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
	
	public static EntrustChangeEnum getEnumByCode(Integer code) {
		for (EntrustChangeEnum entrustsource : EntrustChangeEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource;
			}
		}
		return null;
	}
}
