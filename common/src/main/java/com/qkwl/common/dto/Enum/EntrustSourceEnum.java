package com.qkwl.common.dto.Enum;

public enum EntrustSourceEnum {
	WEB(1, "WEB", "WEB"), // Web
	APP(2, "APP", "APP"), // APP
	API(3, "API", "API"), // API
	HUOBI(4, "HUOBI", "WEB"), // Huobi
	CHBTC(5, "CHBTC", "WEB"); // CHBTC

	private Integer code;
	private Object value;
	private Object frontValue;

	private EntrustSourceEnum(Integer code, Object value, Object frontValue) {
		this.code = code;
		this.value = value;
		this.frontValue = frontValue;
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

	public Object getFrontValue() {
		return frontValue;
	}

	public void setFrontValue(Object frontValue) {
		this.frontValue = frontValue;
	}

	public static String getAdminValueByCode(Integer code) {
		for (EntrustSourceEnum entrustsource : EntrustSourceEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}

	public static String getFrontValueByCode(Integer code) {
		for (EntrustSourceEnum entrustsource : EntrustSourceEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getFrontValue().toString();
			}
		}
		return null;
	}
}
