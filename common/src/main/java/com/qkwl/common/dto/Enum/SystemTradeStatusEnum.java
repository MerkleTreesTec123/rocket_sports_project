package com.qkwl.common.dto.Enum;

/**
 * 交易状态
 * @author LY
 *
 */
public enum SystemTradeStatusEnum {
	NORMAL(1, "平台撮合"),
	ABNORMAL(2, "禁用"),
	HUOBI(3, "火币撮合");

	private Integer code;
	private Object value;

	SystemTradeStatusEnum(Integer code, Object value) {
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
		for (SystemTradeStatusEnum coinType : SystemTradeStatusEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
