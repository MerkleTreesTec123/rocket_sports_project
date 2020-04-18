package com.qkwl.common.dto.Enum;

public enum RechargeAgencyStateEnum {
	Normal(1, "正常"),
	Freeze(2, "禁用");

	private Integer code;
	private String value;

	private RechargeAgencyStateEnum(int code, String value) {
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
	
	public static String getRechargeAgencyStateValueByCode(Integer code) {
		for (RechargeAgencyStateEnum entrustsource : RechargeAgencyStateEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}
}
