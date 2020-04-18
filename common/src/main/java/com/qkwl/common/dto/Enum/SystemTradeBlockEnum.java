package com.qkwl.common.dto.Enum;

/**
 * 交易区域
 */
public enum SystemTradeBlockEnum {

	MAIN(1, "主板","Main Board"),
	STARTUP(2, "游戏版","E-Sport Board");
	private Integer code;
	private Object value;
	private String englishName;

	SystemTradeBlockEnum(Integer code, Object value,String englishName) {
		this.code = code;
		this.value = value;
		this.englishName = englishName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
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
		for (SystemTradeBlockEnum coinType : SystemTradeBlockEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
