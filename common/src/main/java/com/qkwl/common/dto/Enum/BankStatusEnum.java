package com.qkwl.common.dto.Enum;

/**
 * 银行状态枚举
 * @author ZKF
 */
public enum BankStatusEnum {
	
	Enable(1, "启用"),
	Disable(2, "禁用");

	private Integer code;
	private Object value;

	private BankStatusEnum(Integer code, Object value) {
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
}
