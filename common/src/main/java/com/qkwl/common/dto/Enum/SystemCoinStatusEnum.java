package com.qkwl.common.dto.Enum;

/**
 * 币种状态
 * @author LY
 *
 */
public enum SystemCoinStatusEnum {
	NORMAL(1, "正常"), 
	ABNORMAL(2, "禁用");

	private Integer code;
	private Object value;

	private SystemCoinStatusEnum(Integer code, Object value) {
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
		for (SystemCoinStatusEnum coinType : SystemCoinStatusEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
