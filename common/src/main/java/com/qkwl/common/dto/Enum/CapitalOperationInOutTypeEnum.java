package com.qkwl.common.dto.Enum;

/**
 * 资金充值提现类型枚举
 */
public enum CapitalOperationInOutTypeEnum {
	
	IN(1, "充值"),
	OUT(2, "提现");

	private Integer code;
	private String value;

	CapitalOperationInOutTypeEnum(Integer code, String value) {
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
