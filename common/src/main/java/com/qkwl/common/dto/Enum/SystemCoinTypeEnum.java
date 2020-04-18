package com.qkwl.common.dto.Enum;

/**
 * 法币类型
 * @author LY
 */
public enum SystemCoinTypeEnum {
	CNY(1, "法币"), 
	COIN(2, "虚拟币");

	private Integer code;
	private Object value;

	private SystemCoinTypeEnum(Integer code, Object value) {
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
		for (SystemCoinTypeEnum coinType : SystemCoinTypeEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
