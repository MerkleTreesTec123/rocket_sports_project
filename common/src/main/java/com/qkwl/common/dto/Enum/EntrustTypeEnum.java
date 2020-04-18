package com.qkwl.common.dto.Enum;

public enum EntrustTypeEnum {
	BUY(0, "买单"),		// 买单
	SELL(1, "卖单");		// 卖单
	
	private Integer code;
	private String value;
	
	private EntrustTypeEnum(int code, String value) {
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
	
	public static String getEntrustTypeValueByCode(Integer code) {
		for (EntrustTypeEnum entrustType : EntrustTypeEnum.values()) {
			if (entrustType.getCode().equals(code)) {
				return entrustType.getValue().toString();
			}
		}
		return null;
	}
}
