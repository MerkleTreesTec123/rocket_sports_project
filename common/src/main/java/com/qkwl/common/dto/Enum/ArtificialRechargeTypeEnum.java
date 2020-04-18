package com.qkwl.common.dto.Enum;

/**
 * 人工充值
 */
public enum ArtificialRechargeTypeEnum {

	Common(1, "普通充值"),
	Capital(2, "资金处理"),
	Activity(3, "活动赠送");

	private Integer code;
	private String value;

	private ArtificialRechargeTypeEnum(int code, String value) {
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
	
	public static String getTypeValueByCode(Integer code) {
		for (ArtificialRechargeTypeEnum e : ArtificialRechargeTypeEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}
}
